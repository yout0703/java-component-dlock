package yout.component.dlock.aop;

import yout.component.dlock.annotation.DLock;
import yout.component.dlock.exception.DistributedLockException;
import yout.component.dlock.model.LockInfo;
import yout.component.dlock.service.LockInfoGenerator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 分布式锁AOP处理
 * @author: yout0703
 * @date: 2023-06-22
 */
@Aspect
public class DlockAopHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DlockAopHandler.class);

    @Autowired
    private LockInfoGenerator lockInfoGenerator;

    @Autowired
    private RedissonClient redissonClient;

    @Around(value = "@annotation(lockAnno)")
    public Object around(ProceedingJoinPoint joinPoint, DLock lockAnno) throws Throwable {
        LockInfo lockInfo = lockInfoGenerator.get(joinPoint, lockAnno);
        RLock lock = getLock(lockInfo);
        log.info("[{}]Try to get lock with lockInfo = {}.", Thread.currentThread()
                .getName(), lockInfo);
        boolean lockSuccess = lock.tryLock(lockInfo.getWaitTime(), lockInfo.getReleaseTime(), TimeUnit.SECONDS);

        // 如果获取锁失败了，则进入失败的处理逻辑
        if (!lockSuccess) {
            log.warn("[{}]Get lock timeout with lockInfo = ({})", Thread.currentThread()
                    .getName(), lockInfo);

            // 如果自定义了获取锁失败的处理策略，则执行自定义的降级处理策略
            if (StringUtils.hasText(lockAnno.customLockTimeoutStrategy())) {
                return handleCustomLockTimeout(lockAnno.customLockTimeoutStrategy(), joinPoint);
            } else {
                // 否则执行预定义的执行策略
                // 注意：如果没有指定预定义的策略，默认的策略为静默啥不做处理
                lockAnno.lockTimeoutStrategy()
                        .handle(lockInfo, joinPoint);
                return null;
            }
        } else {
            log.info("[{}]Get lock success. Lock({})", Thread.currentThread()
                    .getName(), lockInfo.getName());
        }
        Date startTime = new Date();
        try {
            return joinPoint.proceed();
        } finally {
            try {
                // 如果设置了自动释放时间（autoReleaseTime>0）,而业务逻辑处理时间不够长则等待相应时间后再解锁
                if (lockInfo.getReleaseTime() > 0) {
                    long waitTime = lockInfo.getReleaseTime() * 1000 - (System.currentTimeMillis() - startTime.getTime());
                    if (waitTime > 0) {
                        log.info("[{}]Wait for {}ms to release lock({})", Thread.currentThread()
                                .getName(), waitTime, lockInfo.getName());
                        Thread.sleep(waitTime);
                    }
                }
                // 释放锁
                if (lock.isLocked()) {
                    lock.unlock();
                }
                log.info("[{}]Release lock success. key = {}", Thread.currentThread()
                                .getName(),
                        lockInfo.getName());
            } catch (Exception e) {
                log.warn("release lock fail,try handle release timeout.  lockName= {}. e = {}", lockInfo.getName(),
                        e.getMessage());
                handleReleaseTimeout(lockAnno, lockInfo, joinPoint);
            }
        }
    }

    private RLock getLock(LockInfo lockInfo) {
        return switch (lockInfo.getLockModel()) {
            case FAIR -> redissonClient.getFairLock(lockInfo.getName());
            case READ -> redissonClient.getReadWriteLock(lockInfo.getName())
                    .readLock();
            case WRITE -> redissonClient.getReadWriteLock(lockInfo.getName())
                    .writeLock();
            default -> redissonClient.getLock(lockInfo.getName());
        };
    }

    /**
     * 处理自定义加锁超时
     */
    private Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget()
                    .getClass()
                    .getDeclaredMethod(lockTimeoutHandler,
                            currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res = null;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new DistributedLockException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return res;
    }

    /**
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(DLock lockAnno, LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {

        log.warn("Timeout while release Lock({})", lockInfo.getName());

        if (StringUtils.hasText(lockAnno.customReleaseTimeoutStrategy())) {
            handleCustomReleaseTimeout(lockAnno.customReleaseTimeoutStrategy(), joinPoint);
        } else {
            lockAnno.releaseTimeoutStrategy()
                    .handle(lockInfo);
        }
    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget()
                    .getClass()
                    .getDeclaredMethod(releaseTimeoutHandler,
                            currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new DistributedLockException(
                    "Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}

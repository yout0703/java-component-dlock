package yout.component.dlock.service;

import yout.component.dlock.annotation.DLock;
import yout.component.dlock.model.LockInfo;
import yout.component.dlock.model.LockModelEnum;
import yout.component.dlock.springboot.DlockConfig;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @description: 获取LockInfo
 * @author: yout0703
 * @date: 2023-06-22
 */
public class LockInfoGenerator {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LockInfoGenerator.class);

    @Autowired
    private BusinessKeyProvider businessKeyProvider;

    @Autowired
    private DlockConfig dLockConfig;

    @Value("${spring.application.name:default-app}")
    private final String appName = "default-app";
    private static final String LOCK_NAME_SEPARATOR = ".";

    public LockInfo get(JoinPoint joinPoint, DLock lock) {
        // 方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LockModelEnum type = lock.lockModel();
        String businessKeyName = businessKeyProvider.getKeyName(joinPoint, lock);

        // 应用级 -> 方法级 -> 业务数据级
        StringBuilder lockName = new StringBuilder(appName);
        lockName.append(LOCK_NAME_SEPARATOR).append(joinPoint.getThis().getClass().getGenericSuperclass()).append(LOCK_NAME_SEPARATOR)
                .append(signature.getMethod().getName()).append(LOCK_NAME_SEPARATOR).append(businessKeyName);
        long waitToLockTime = getWaitTime(lock);
        long autoReleaseTime = getAutoReleaseTime(lock);

        if (autoReleaseTime == -1) {
            log.warn("锁：{}，没有设置自动释放时间，会尝试一直等待，请确认是否满足预期。", lockName);
        }
        return new LockInfo(type, lockName.toString(), waitToLockTime, autoReleaseTime);
    }

    private long getWaitTime(DLock lock) {
        return lock.waitToLockTime() == Long.MIN_VALUE ? dLockConfig.getWaitToLockTime() : lock.waitToLockTime();
    }

    private long getAutoReleaseTime(DLock lock) {
        return lock.autoReleaseTime() == Long.MIN_VALUE ? dLockConfig.getAutoReleaseTime() : lock.autoReleaseTime();
    }

}

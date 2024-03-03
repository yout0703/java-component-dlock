package yout.component.dlock.annotation;


import yout.component.dlock.model.LockModelEnum;
import yout.component.dlock.strategy.GetLockTimeoutHandler;
import yout.component.dlock.strategy.LockGetLockTimeoutStrategy;
import yout.component.dlock.strategy.ReleaseTimeoutStrategy;
import yout.component.dlock.strategy.ReleaseLockTimeoutHandler;

import java.lang.annotation.*;

/**
 * @description: 分布式锁注解
 * @author: yout0703
 * @date: 2023-06-22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DLock {
    /**
     * 业务key，用于最终生成Redis锁的key的一个粒度，支持SePL表达式<br />
     *
     * @return keys
     */
    String[] keys() default {};

    /**
     * 锁的模式:如果不设置,默认可重入非公平锁
     */
    LockModelEnum lockModel() default LockModelEnum.REENTRANT;

    /**
     * 锁自动释放的时间，默认任务执行完才释放，如果任务执行时间小于设置的释放时间，会等待释放时间到才释放；单位秒
     */
    long autoReleaseTime() default Long.MIN_VALUE;

    /**
     * 等待加锁超时时间，默认60s,单位秒
     */
    long waitToLockTime() default Long.MIN_VALUE;

    /**
     * 加锁超时的处理策略
     *
     * @return lockTimeoutStrategy
     *
     */
    LockGetLockTimeoutStrategy lockTimeoutStrategy() default LockGetLockTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义加锁超时的处理策略类的全路径 <br />
     * 自定义类需要实现接口{@link GetLockTimeoutHandler}
     *
     * @return customLockTimeoutStrategy
     */
    String customLockTimeoutStrategy() default "";

    /**
     * 释放锁时已超时的处理策略
     *
     * @return releaseTimeoutStrategy
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy() default ReleaseTimeoutStrategy.NO_OPERATION;

    /**
     * 自定义释放锁时已超时的处理策略<br />
     * 需要实现接口{@link ReleaseLockTimeoutHandler}
     *
     * @return customReleaseTimeoutStrategy
     */
    String customReleaseTimeoutStrategy() default "";
}

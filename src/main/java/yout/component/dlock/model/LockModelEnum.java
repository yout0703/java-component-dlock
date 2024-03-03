package yout.component.dlock.model;

/**
 * @author : yout0703
 * @description: 分布式锁的类型
 * @date: 2023-6-17
 */
public enum LockModelEnum {
    /**
     * 可重入锁(非公平)
     */
    REENTRANT,
    /**
     * 公平锁
     */
    FAIR,
    /**
     * 读锁
     */
    READ,
    /**
     * 写锁
     */
    WRITE,
}

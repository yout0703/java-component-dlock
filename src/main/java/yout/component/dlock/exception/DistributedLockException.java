package yout.component.dlock.exception;

/**
 * @description: 分布式锁相关异常
 * @author: yout0703
 * @date: 2023-06-17
 */
public class DistributedLockException extends Exception {

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(String message, Exception e) {
        super(message, e);
    }
}

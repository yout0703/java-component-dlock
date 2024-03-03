package yout.component.dlock.exception;

/**
 * @description: 分布式锁超时异常
 * @author: yout0703
 * @date: 2023-06-23
 */
public class DistributedLockTimeoutException extends RuntimeException {

    public DistributedLockTimeoutException(String message) {
        super(message);
    }

    public DistributedLockTimeoutException(String message, Exception e) {
        super(message, e);
    }
}

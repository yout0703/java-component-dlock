package yout.component.dlock.strategy;


import yout.component.dlock.exception.DistributedLockTimeoutException;
import yout.component.dlock.model.LockInfo;

/**
 * @description: 释放锁超时处理策略
 * @author: yout0703
 * @date: 2023-06-23
 */
public enum ReleaseTimeoutStrategy implements ReleaseLockTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) {

            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s",
                    lockInfo.getName(), lockInfo.getReleaseTime());
            throw new DistributedLockTimeoutException(errorMsg);
        }
    }
}

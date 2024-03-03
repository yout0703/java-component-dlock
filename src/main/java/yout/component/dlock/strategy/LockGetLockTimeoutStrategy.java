package yout.component.dlock.strategy;

import yout.component.dlock.exception.DistributedLockTimeoutException;
import yout.component.dlock.model.LockInfo;
import org.aspectj.lang.JoinPoint;

/**
 * @description: 加锁超时的处理策略
 * @author: yout0703
 * @date: 2023-06-23
 */
public enum LockGetLockTimeoutStrategy implements GetLockTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, JoinPoint joinPoint) {
            // do nothing
        }
    },

    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo, JoinPoint joinPoint) {

            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%ds)", lockInfo.getName(),
                lockInfo.getWaitTime());
            throw new DistributedLockTimeoutException(errorMsg);
        }
    }
}

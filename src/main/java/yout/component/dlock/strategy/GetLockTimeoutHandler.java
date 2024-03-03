package yout.component.dlock.strategy;

import yout.component.dlock.model.LockInfo;
import org.aspectj.lang.JoinPoint;

/**
 * @description: 获取超时处理策略
 * @author: yout0703
 * @date: 2023-06-23
 */
public interface GetLockTimeoutHandler {

    /**
     * 处理超时
     *
     * @param lockInfo
     *            锁信息
     * @param joinPoint
     *            切入点
     */
    void handle(LockInfo lockInfo, JoinPoint joinPoint);
}

package yout.component.dlock.strategy;


import yout.component.dlock.model.LockInfo;

/**
 * @description: 释放锁超时处理策略
 * @author: yout0703
 * @date: 2023-06-23
 */
public interface ReleaseLockTimeoutHandler {

    /**
     * 处理超时
     *
     * @param lockInfo
     *            锁信息
     */
    void handle(LockInfo lockInfo);
}

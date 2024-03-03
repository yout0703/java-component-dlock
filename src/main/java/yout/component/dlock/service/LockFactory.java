package yout.component.dlock.service;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import yout.component.dlock.lock.*;
import yout.component.dlock.model.LockInfo;

/**
 * @description: 生成锁工厂
 * @author: yout0703
 * @date: 2023-06-22
 */
public class LockFactory {

    @Autowired
    private RedissonClient redissonClient;

    public Lock getLock(LockInfo lockInfo) {
        switch (lockInfo.getLockModel()) {
            case FAIR:
                return new FairLock(redissonClient, lockInfo);
            case READ:
                return new ReadLock(redissonClient, lockInfo);
            case WRITE:
                return new WriteLock(redissonClient, lockInfo);
            default:
                return new ReentrantLock(redissonClient, lockInfo);
        }
    }
}

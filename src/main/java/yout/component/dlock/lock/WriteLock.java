package yout.component.dlock.lock;

import yout.component.dlock.model.LockInfo;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @description: 写锁
 * @author: yout0703
 * @date: 2023-06-23
 */
public class WriteLock implements Lock {
    private RReadWriteLock rLock;

    private final LockInfo lockInfo;

    private final RedissonClient redissonClient;

    public WriteLock(RedissonClient redissonClient, LockInfo info) {
        this.redissonClient = redissonClient;
        this.lockInfo = info;
    }

    @Override
    public boolean acquire() {
        try {
            rLock = redissonClient.getReadWriteLock(lockInfo.getName());
            return rLock.writeLock().tryLock(lockInfo.getWaitTime(), lockInfo.getReleaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release() {
        try {
            rLock.writeLock().unlock();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

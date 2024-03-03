package yout.component.dlock.lock;

import yout.component.dlock.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @description: 公平锁
 * @author: yout0703
 * @date: 2023-06-23
 */
public class FairLock implements Lock {

    private RLock rLock;

    private final LockInfo lockInfo;

    private final RedissonClient redissonClient;

    public FairLock(RedissonClient redissonClient, LockInfo info) {
        this.redissonClient = redissonClient;
        this.lockInfo = info;
    }

    @Override
    public boolean acquire() {
        try {
            this.rLock = redissonClient.getFairLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getReleaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release() {
        try {
            rLock.unlock();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

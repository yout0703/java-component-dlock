package yout.component.dlock.model;

/**
 * @description: 锁的数据模型
 * @author: yout0703
 * @date: 2023-06-22
 */
public class LockInfo {
    private LockModelEnum lockModel;
    private String name;
    private long waitTime;
    private long releaseTime;

    public LockInfo(LockModelEnum lockModel, String name, long waitTime, long releaseTime) {
        this.lockModel = lockModel;
        this.name = name;
        this.waitTime = waitTime;
        this.releaseTime = releaseTime;
    }

    public LockInfo() {
    }

    public LockModelEnum getLockModel() {
        return lockModel;
    }

    public void setLockModel(LockModelEnum lockModel) {
        this.lockModel = lockModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    @Override
    public String toString() {
        return "LockInfo{" +
               "lockModel=" + lockModel +
               ", name='" + name + '\'' +
               ", waitTime=" + waitTime +
               ", releaseTime=" + releaseTime +
               '}';
    }
}

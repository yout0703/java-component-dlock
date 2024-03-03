package yout.component.dlock.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: yout0703
 * @date: 2023-06-22
 */
@ConfigurationProperties(prefix = DlockConfig.PREFIX)
public class DlockConfig {

    public static final String PREFIX = "yout.dlock";

    /**
     * Redisson 相关配置
     */
    private boolean enabled = true;
    private String address;
    private String password;
    private int database = 8;
    private ClusterServer clusterServer;
    private String codec = "org.redisson.codec.JsonJacksonCodec";

    /**
     * lock 相关配置
     */
    private long waitToLockTime = 60;
    private long autoReleaseTime = 60;

    public static class ClusterServer {
        private String[] nodeAddresses;

        public String[] getNodeAddresses() {
            return nodeAddresses;
        }

        public void setNodeAddresses(String[] nodeAddresses) {
            this.nodeAddresses = nodeAddresses;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public ClusterServer getClusterServer() {
        return clusterServer;
    }

    public void setClusterServer(ClusterServer clusterServer) {
        this.clusterServer = clusterServer;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public long getWaitToLockTime() {
        return waitToLockTime;
    }

    public void setWaitToLockTime(long waitToLockTime) {
        this.waitToLockTime = waitToLockTime;
    }

    public long getAutoReleaseTime() {
        return autoReleaseTime;
    }

    public void setAutoReleaseTime(long autoReleaseTime) {
        this.autoReleaseTime = autoReleaseTime;
    }
}

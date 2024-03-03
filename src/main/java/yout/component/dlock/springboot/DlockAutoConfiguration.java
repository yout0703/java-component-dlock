package yout.component.dlock.springboot;

import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;
import yout.component.dlock.aop.DlockAopHandler;
import yout.component.dlock.service.BusinessKeyProvider;
import yout.component.dlock.service.LockFactory;
import yout.component.dlock.service.LockInfoGenerator;

/**
 * @description: DLock自动装配
 * @author: yout0703
 * @date: 2023-06-22
 */
@Configuration
@ConditionalOnProperty(prefix = DlockConfig.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(DlockConfig.class)
@Import({DlockAopHandler.class})
public class DlockAutoConfiguration {

    @Autowired
    private DlockConfig dLockConfig;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    RedissonClient redisson() throws Exception {
        Config config = new Config();
        if (dLockConfig.getClusterServer() != null) {
            config.useClusterServers().setPassword(dLockConfig.getPassword())
                    .addNodeAddress(dLockConfig.getClusterServer().getNodeAddresses());
        } else {
            config.useSingleServer().setAddress(dLockConfig.getAddress()).setDatabase(dLockConfig.getDatabase())
                    .setPassword(dLockConfig.getPassword());
        }
        Codec codec =
                (Codec) ClassUtils.forName(dLockConfig.getCodec(), ClassUtils.getDefaultClassLoader()).getDeclaredConstructor().newInstance();
        config.setCodec(codec);
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }

    @Bean
    public LockFactory lockFactory() {
        return new LockFactory();
    }

    @Bean
    public LockInfoGenerator lockInfoProvider() {
        return new LockInfoGenerator();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider() {
        return new BusinessKeyProvider();
    }
}

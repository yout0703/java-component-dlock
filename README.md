# java-component-dlock
> 提供基于注解的分布式锁功能。该组件依赖 Redis 环境。

1. Features

- 基于注解使用分布式锁，使用简单，侵入性小。
- 支持配置分布式锁类型：可重入锁、读锁、写锁、公平锁等。
- 支持配置获取锁、保持锁以及释放锁的超时时间。
- 支持获取锁和释放锁时出错的策略配置。

2. 使用方法
引入依赖
```xml
<dependency>
    <groupId>yout.component</groupId>
    <artifactId>java-component-pgextend</artifactId>
    <version>1.0.0</version>
</dependency>
```
首先配置 Redis 环境，在 application.xml 中添加相关配置：
```yaml
yout:
  dlock:
    address: redis://${REDIS_HOST}:${REDIS_PORT}
    password: ${REDIS_PASS}
    database: ${REDIS_DB}
```
在需要加锁的方法上使用 @Dlock 注解即可。
```java
@DLock(autoReleaseTime = 60, waitToLockTime = 60)
public void doSomething(String queueName, BaseTask taskBean) {
    // do something.
}
```
具体的注解参数可以参考源代码中的注释：
```text
yout.component.dlock.annotation.DLock
```
3. 注意事项
由于基于注解的分布式锁实现逻辑是使用反向代理调用目标方法，所以与 @Async 等注解一样，不能在同一个类中调用目标方法，否则不会生效。

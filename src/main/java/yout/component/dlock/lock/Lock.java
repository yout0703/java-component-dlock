package yout.component.dlock.lock;

/**
 * @description: 锁接口
 * @author: yout0703
 * @date: 2023-06-23
 */
public interface Lock {

    /**
     * 请求锁
     * @return 是否成功
     */
    boolean acquire();

    /**
     * 释放锁
     * @return 是否成功
     */
    boolean release();
}

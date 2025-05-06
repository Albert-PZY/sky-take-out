package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@Order(0) //提升该切面类的执行优先级
public class LockAspect {

    private static final String FAIR_LOCK = "lock"; //锁使用的对象
    public static final long WATING_TIME = 60; //尝试加锁的等待时间
    @Autowired
    RedissonClient redissonClient;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.Lock)")
    public void readWriteLockPointcut() {
    }

    /**
     * 环绕通知，在通知中进行分布式锁的加锁和解锁
     *
     * @param proceedingJoinPoint
     */
    @Around("readWriteLockPointcut()")
    public Object readWriteLock(ProceedingJoinPoint proceedingJoinPoint) {
        //获得锁对象
        RLock lock = redissonClient.getLock(FAIR_LOCK);
        try {
            boolean success = lock.tryLock(WATING_TIME, TimeUnit.SECONDS); //尝试加锁，等待WATING_TIME秒
            if (success) {
                log.info("线程{}加锁成功", Thread.currentThread().getName());
            } else {
                log.info("线程{}加锁失败", Thread.currentThread().getName());
            }
            return proceedingJoinPoint.proceed(); //执行原始方法
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock(); //最后释放锁
            log.info("线程{}释放锁", Thread.currentThread().getName());
        }
    }
}
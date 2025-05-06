package com.sky.aspect;

import com.sky.constant.StatusConstant;
import com.sky.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Aspect
@Component
@Slf4j
public class UpdateBloomFilterAspect {

    @Autowired
    private ApplicationContext applicationContext;
    @Value("${spring.bloom-filter.expected-insertions}")
    private long expectedInsertions;
    @Value("${spring.bloom-filter.false-probability}")
    private double falseProbability;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.UpdateBloomFilter)")
    public void updateBloomFilterPointcut() {
    }

    /**
     * 后置通知，在通知中进行布隆过滤器的更新
     */
    @After("updateBloomFilterPointcut()")
    public void updateBloomFilter(JoinPoint joinPoint) {
        log.info("开始更新布隆过滤器");

        //获得布隆过滤器的Bean对象
        RBloomFilter<Integer> bloomFilter = (RBloomFilter<Integer>) applicationContext.getBean("bloomFilter");
        //清理布隆过滤器
        bloomFilter.expire(Instant.now());
        bloomFilter.clearExpire();
        //初始化布隆过滤器
        bloomFilter.tryInit(expectedInsertions, falseProbability);

        log.info("开始预热布隆过滤器...");

        //查询所有的分类id
        List<Integer> CategoryIds = categoryMapper.getCategoryIdsByStatus(StatusConstant.ENABLE);

        //预热布隆过滤器
        bloomFilter.add(CategoryIds);
    }
}
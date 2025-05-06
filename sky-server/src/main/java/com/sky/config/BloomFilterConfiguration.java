package com.sky.config;

import com.sky.constant.StatusConstant;
import com.sky.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class BloomFilterConfiguration {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CategoryMapper categoryMapper;
    @Value("${spring.bloom-filter.expected-insertions}")
    private long expectedInsertions;
    @Value("${spring.bloom-filter.false-probability}")
    private double falseProbability;

    /**
     * 创建并预热布隆过滤器
     */
    @Bean("bloomFilter")
    public RBloomFilter<Integer> init() {
        log.info("开始创建布隆过滤器...");
        RBloomFilter<Integer> bloomFilter = redissonClient.getBloomFilter("BloomFilter", StringCodec.INSTANCE);
        bloomFilter.tryInit(expectedInsertions, falseProbability);

        log.info("开始预热布隆过滤器...");

        //查询所有的分类id
        List<Integer> CategoryIds = categoryMapper.getCategoryIdsByStatus(StatusConstant.ENABLE);

        //预热布隆过滤器
        bloomFilter.add(CategoryIds);

        return bloomFilter;
    }
}
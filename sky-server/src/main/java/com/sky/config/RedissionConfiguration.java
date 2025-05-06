package com.sky.config;

import com.sky.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedissionConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        log.info("开始创建redisson客户端对象...");

        //拼接redis地址
        StringBuffer address = new StringBuffer("redis://");
        address.append(redisProperties.getHost()).append(":").append(redisProperties.getPort());

        //创建并配置redisson客户端对象
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE)
                .useSingleServer()
                .setAddress(address.toString())
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }
}
package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisProperties {

    /**
     * redis相关配置
     */
    private String host;
    private String port;
    private String password;
    private int database;

}
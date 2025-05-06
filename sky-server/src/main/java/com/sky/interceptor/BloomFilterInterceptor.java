package com.sky.interceptor;

import com.sky.constant.MessageConstant;
import com.sky.exception.CategoryIdNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class BloomFilterInterceptor implements HandlerInterceptor {

    @Autowired
    private RBloomFilter bloomFilter;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从查询参数中获取分类id
        String categoryId = request.getQueryString().split("=")[1];

        //2、校验分类id
        try {
            log.info("布隆过滤器校验：{}", categoryId);
            if (bloomFilter.contains(Integer.valueOf(categoryId))) {
                //3、通过，放行
                log.info("布隆过滤器校验通过");
                return true;
            } else {
                //4、不通过，抛出分类不存在异常
                log.info("布隆过滤器校验不通过");
                throw new CategoryIdNotFoundException(MessageConstant.CATEGORY_ID_NOT_FOUND);
            }
        } catch (Exception ex) {
            //4、并响应404状态码
            response.setStatus(404);
            return false;
        }
    }
}
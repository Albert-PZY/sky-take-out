package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return User
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}

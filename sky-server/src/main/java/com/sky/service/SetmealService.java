package com.sky.service;

import com.sky.dto.SetmealDTO;
import org.springframework.stereotype.Service;

@Service
public interface SetmealService {

    void saveWithDish(SetmealDTO setmealDTO);
}

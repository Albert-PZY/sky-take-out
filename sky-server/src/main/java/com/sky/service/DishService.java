package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void insertWithFlavors(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByBatch(List<Long> ids);
}

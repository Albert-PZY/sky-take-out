package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据删除id查询关联套餐id
     * @param dishIds
     * @return List<Long>
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}

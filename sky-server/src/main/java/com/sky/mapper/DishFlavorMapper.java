package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量新增口味
     *
     * @param dishFlavors
     */
    void insertBatch(List<DishFlavor> dishFlavors);
}

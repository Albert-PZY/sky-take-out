package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据删除id查询关联套餐id
     * @param dishIds
     * @return List<Long>
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量保存套餐和菜品之间的关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐和菜品关系
     * @param setmealIds
     */
    void deleteByBatch(List<Long> setmealIds);

    /**
     * 根据套餐id查询套餐和菜品关系
     * @param id
     * @return List<SetmealDish>
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据setmealId删除关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}

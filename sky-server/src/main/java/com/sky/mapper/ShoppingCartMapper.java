package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     *
     * @param shoppingCart
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id修改商品数量
     *
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 添加商品到购物车
     *
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time, number) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime},#{number})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户Id删除购物车
     *
     * @param currentId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long currentId);

    /**
     * 根据菜品Id删除数据
     *
     * @param dishId
     */
    @Delete("delete from shopping_cart where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据套餐Id删除数据
     *
     * @param setmealId
     */
    @Delete("delete from shopping_cart where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}

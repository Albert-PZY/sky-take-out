package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void insertWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (!dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
        }
        dishFlavorMapper.insertBatch(dishFlavors);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return new PageResult(total, result);
    }

    @Transactional
    @Override
    public void deleteByBatch(List<Long> ids) {
        //批量删除菜品 - 在售不能删
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //批量删除菜品 - 套餐内不能删
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (!setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        //删除菜品
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品相应口味
//            dishFlavorMapper.deleteByDishId(id);
//        }

        //批量删除菜品
        dishMapper.deleteByIds(ids);
        //批量删除菜品对应口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        //查询菜品
        Dish dish = dishMapper.getById(id);
        //查询菜品口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //封装并返回VO对象
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品，先不需要flavor属性，所以用Dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除菜品原有口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重写菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
        }
        //向口味表插入n条数据
        dishFlavorMapper.insertBatch(flavors);
    }
}

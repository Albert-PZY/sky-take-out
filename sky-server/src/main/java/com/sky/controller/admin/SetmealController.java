package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return Result
     */
    @ApiOperation("新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return Result<PageResult>
     */
    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return Result
     */
    @ApiOperation("批量删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐:{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据Id查询套餐和关联菜品数据
     * @param id
     * @return Result<SetmealVO>
     */
    @ApiOperation("根据Id查询套餐和关联菜品数据")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据Id查询套餐和关联菜品数据:{}",id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return Result
     */
    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐:{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return Result
     */
    @ApiOperation("套餐起售停售")
    @PostMapping("/status/{status}")
    public Result stratOrStop(@PathVariable Integer status, Long id) {
        log.info("套餐起售停售:{},{}",status,id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }

}
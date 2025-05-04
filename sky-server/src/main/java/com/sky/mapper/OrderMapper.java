package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     *
     * @param orderStatus
     * @param orderPaidStatus
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, String orderNumber);

    /**
     * 分页条件查询
     * @param ordersPageQueryDTO
     * @return Page<Orders>
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return Orders
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 统计各状态订单数量
     * @param status
     * @return Integer
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatics(Integer status);

    /**
     * 定时处理超时订单
     *
     * @param status
     * @param time
     * @return List<Orders>
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    /**
     * 根据订单号和用户Id查询
     * @param outTradeNo
     * @param currentId
     * @return Orders
     */
    @Select("select * from orders where number = #{outTradeNo} and user_id = #{currentId}")
    Orders getByNumberAndUserId(String outTradeNo, Long currentId);

    /**
     * 根据时间和订单状态查询统计当天营业额
     * @param map
     * @return Double
     */
    Double sumByMap(Map map);

    /**
     * 根据时间统计用户
     * @param map
     * @return Integer
     */
    Integer countByMap(Map map);
}

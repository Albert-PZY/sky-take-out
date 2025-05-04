package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface ReportService {

    /**
     * 根据日期统计营业额数据
     *
     * @param begin
     * @param end
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计订单统计
     *
     * @param begin
     * @param end
     * @return UserReportVO
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return OrderReportVO
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
}
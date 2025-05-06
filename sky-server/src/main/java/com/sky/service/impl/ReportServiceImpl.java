package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    //优化后
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //存放从 begin 到 end 范围的日期集合
        List<LocalDate> dateList = getDateList(begin, end);
        //存放从 begin 到 end 范围每天的营业额
        BigDecimal[] turnoverList = new BigDecimal[dateList.size()];

        //初始化数组
        Arrays.fill(turnoverList, BigDecimal.ZERO);

        //一次性查出 begin 到 end 范围 已完成 的订单集合
        Map map = new HashMap();
        map.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        map.put("status", Orders.COMPLETED);
        List<Orders> ordersList = orderMapper.getByMap(map);

        //计算当天营业额
        ordersList.forEach(order -> {
            LocalDate localDate = order.getOrderTime().toLocalDate();
            //数组下标 → 当天距离开始计算的日期是第几天
            int period = Period.between(begin, localDate).getDays();
            //按日期累加当天营业额
            turnoverList[period] = turnoverList[period].add(order.getAmount());
        });

        String dataListStr = StringUtils.join(dateList, ",");
        String turnoverListStr = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO
                .builder()
                .dateList(dataListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    //原始版
    /*@Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }*/

    //优化版
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end); //当前集合用于存放从begin到end范围内每天的日期
        int[] totalUserList = new int[dateList.size()]; //当前集合用于存放从begin到end范围内每天的总用户数
        int[] newUserList = new int[dateList.size()]; //当前集合用于存放从begin到end范围内每天新增的用户数

        //查询注册时间在begin到end范围内的所有用户数据
        List<User> userList = userMapper.getByBeginAndEndTime(
                LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX));

        //根据查询到的用户数据计算每天的新增用户数
        for (User user : userList) {
            LocalDate createTime = user.getCreateTime().toLocalDate();
            int period = Period.between(begin, createTime).getDays();
            newUserList[period]++;
        }

        //查询begin时间之前的总用户数
        Map map = new HashMap<>();
        map.put("end", LocalDateTime.of(begin, LocalTime.MIN));
        Integer totalUser = userMapper.countByMap(map);

        //计算每天的总用户数
        totalUserList[0] = totalUser + newUserList[0];
        for (int i = 1; i < dateList.size(); i++) {
            totalUserList[i] = totalUserList[i - 1] + newUserList[i];
        }

        //将集合转换为字符串
        String dateListString = StringUtils.join(dateList, ",");
        String totalUserListString = StringUtils.join(totalUserList, ',');
        String newUserListString = StringUtils.join(newUserList, ',');

        //构造UserReportVO并返回
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(dateListString)
                .totalUserList(totalUserListString)
                .newUserList(newUserListString)
                .build();
        return userReportVO;
    }


    //原始版
    /*@Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            Integer totalUser = userMapper.countByMap(map);
            map.put("end", endTime);
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser.toString());
            newUserList.add(newUser.toString());
        });
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }*/

    //优化版
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end); //当前集合用于存放从begin到end范围内每天的日期
        int[] orderCountList = new int[dateList.size()]; //当前集合用于存放从begin到end范围内每天的订单数
        int[] validOrderCountList = new int[dateList.size()]; //当前集合用于存放从begin到end范围内每天的有效订单数

        //查询从begin到end范围内的所有订单数据
        Map map = new HashMap<>();
        map.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        List<Orders> ordersList = orderMapper.getByMap(map);

        //把查出来的订单数以及已完成的订单数加上
        for (Orders orders : ordersList) {
            LocalDate orderTime = orders.getOrderTime().toLocalDate();
            int period = Period.between(begin, orderTime).getDays();
            orderCountList[period]++;
            if (orders.getStatus().equals(Orders.COMPLETED)) {
                validOrderCountList[period]++;
            }
        }

        Integer totalOrderCount = Arrays.stream(orderCountList).reduce(Integer::sum).getAsInt(); //计算订单总数
        Integer validOrderCount = Arrays.stream(validOrderCountList).reduce(Integer::sum).getAsInt(); //计算有效订单总数
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount; //计算订单完成率

        //将集合转换为字符串
        String dateListString = StringUtils.join(dateList, ",");
        String orderCountListString = StringUtils.join(orderCountList, ',');
        String validOrderCountListString = StringUtils.join(validOrderCountList, ',');

        //构造OrderReportVO并返回
        return OrderReportVO.builder()
                .dateList(dateListString)
                .orderCountList(orderCountListString)
                .validOrderCountList(validOrderCountListString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    //原始版
    /*@Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> validOrderList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询当天订单数
            Integer totalOrder = getOrderCount(beginTime, endTime, null);
            //查询当天有效订单数
            Integer validOrder = getOrderCount(beginTime, endTime, totalOrder);
            totalOrderList.add(totalOrder);
            validOrderList.add(validOrder);
        });
        Integer totalOrderCount = totalOrderList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = totalOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }*/

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = getTop10(beginTime, endTime, Orders.COMPLETED);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询获取数据
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.of(beginTime, LocalTime.MIN),
                LocalDateTime.of(endTime, LocalTime.MAX));
        //将数据写入Excel表格
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于模板文件创建新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格文件的 Sheet 页
            XSSFSheet sheet = excel.getSheetAt(0);
            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginTime + "至" + endTime);
            //获取第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获取第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginTime.plusDays(i);
                businessData = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //以流的形式输出Excel表格
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.close();
            excel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装返回日期集合
     *
     * @param begin
     * @param end
     * @return List<LocalDate>
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 封装返回销量前 10 商品集合
     *
     * @param beginTime
     * @param endTime
     * @param status
     * @return List<GoodsSalesDTO>
     */
    private List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);
        return orderMapper.getSalesTop10(map);
    }
}
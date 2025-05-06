package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    void insert(User user);

    /**
     * 根据时间统计用户
     *
     * @param map
     * @return Integer
     */
    Integer countByMap(Map map);

    /**
     * 查询注册时间在 begin 到 end 期间的用户
     *
     * @param begin
     * @param end
     * @return List<User>
     */
    List<User> getByBeginAndEndTime(LocalDateTime begin, LocalDateTime end);

}

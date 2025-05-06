package com.demo.backend.mapper;

import com.demo.backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT id, username, password, email, phone, avatar, role, create_time as createTime, update_time as updateTime FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT id, username, password, email, phone, avatar, role, create_time as createTime, update_time as updateTime FROM user WHERE id = #{id}")
    User findById(Integer id);
    
    @Insert("INSERT INTO user(username, password, email, phone, avatar, role) VALUES(#{username}, #{password}, #{email}, #{phone}, #{avatar}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET username = #{username}, email = #{email}, phone = #{phone}, avatar = #{avatar} WHERE id = #{id}")
    int update(User user);
    
    @Update("UPDATE user SET password = #{password} WHERE username = #{username} AND email = #{email}")
    int updatePassword(@Param("username") String username, @Param("email") String email, @Param("password") String password);
    
    @Select("SELECT id, username, password, email, phone, avatar, role, create_time as createTime, update_time as updateTime FROM user ORDER BY id")
    List<User> findAll();
    
    @Select("SELECT id, username, password, email, phone, avatar, role, create_time as createTime, update_time as updateTime FROM user WHERE email = #{email}")
    User findByEmail(String email);
    
    @Delete("DELETE FROM user WHERE id = #{id}")
    int delete(Integer id);
} 
package com.demo.backend.mapper;

import com.demo.backend.entity.Child;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChildMapper {
    
    @Select("SELECT id, user_id as userId, name, age, gender, avatar, create_time as createTime, update_time as updateTime FROM child WHERE user_id = #{userId}")
    List<Child> findByUserId(Integer userId);
    
    @Select("SELECT id, user_id as userId, name, age, gender, avatar, create_time as createTime, update_time as updateTime FROM child WHERE id = #{id}")
    Child findById(Integer id);
    
    @Insert("INSERT INTO child(user_id, name, age, gender, avatar) VALUES(#{userId}, #{name}, #{age}, #{gender}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Child child);
    
    @Update("UPDATE child SET name = #{name}, age = #{age}, gender = #{gender}, avatar = #{avatar} WHERE id = #{id}")
    int update(Child child);
    
    @Delete("DELETE FROM child WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT COUNT(*) FROM child WHERE user_id = #{userId}")
    int countByUserId(Integer userId);
} 
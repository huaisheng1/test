package com.demo.backend.mapper;

import com.demo.backend.entity.LearningContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LearningContentMapper {
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM learning_content ORDER BY id DESC")
    List<LearningContent> findAll();
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM learning_content WHERE id = #{id}")
    LearningContent findById(Integer id);
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM learning_content WHERE type = #{type} ORDER BY id DESC")
    List<LearningContent> findByType(String type);
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM learning_content WHERE age_range LIKE CONCAT('%', #{age}, '%') ORDER BY id DESC")
    List<LearningContent> findByAge(Integer age);
    
    @Insert("INSERT INTO learning_content(title, content, type, age_range, image_url) VALUES(#{title}, #{content}, #{type}, #{ageRange}, #{imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LearningContent learningContent);
    
    @Update("UPDATE learning_content SET title = #{title}, content = #{content}, type = #{type}, age_range = #{ageRange}, image_url = #{imageUrl} WHERE id = #{id}")
    int update(LearningContent learningContent);
    
    @Delete("DELETE FROM learning_content WHERE id = #{id}")
    int delete(Integer id);
} 
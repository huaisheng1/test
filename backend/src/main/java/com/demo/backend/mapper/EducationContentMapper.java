package com.demo.backend.mapper;

import com.demo.backend.entity.EducationContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EducationContentMapper {
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM education_content ORDER BY id DESC")
    List<EducationContent> findAll();
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM education_content WHERE id = #{id}")
    EducationContent findById(Integer id);
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM education_content WHERE type = #{type} ORDER BY id DESC")
    List<EducationContent> findByType(String type);
    
    @Select("SELECT id, title, content, type, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM education_content WHERE age_range LIKE CONCAT('%', #{age}, '%') ORDER BY id DESC")
    List<EducationContent> findByAge(Integer age);
    
    @Insert("INSERT INTO education_content(title, content, type, age_range, image_url) VALUES(#{title}, #{content}, #{type}, #{ageRange}, #{imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EducationContent educationContent);
    
    @Update("UPDATE education_content SET title = #{title}, content = #{content}, type = #{type}, age_range = #{ageRange}, image_url = #{imageUrl} WHERE id = #{id}")
    int update(EducationContent educationContent);
    
    @Delete("DELETE FROM education_content WHERE id = #{id}")
    int delete(Integer id);
} 
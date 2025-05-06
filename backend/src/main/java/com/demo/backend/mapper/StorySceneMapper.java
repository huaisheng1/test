package com.demo.backend.mapper;

import com.demo.backend.entity.StoryScene;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StorySceneMapper {
    
    @Select("SELECT id, title, description, type, theme, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM story_scene ORDER BY id DESC")
    List<StoryScene> findAll();
    
    @Select("SELECT id, title, description, type, theme, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM story_scene WHERE id = #{id}")
    StoryScene findById(Integer id);
    
    @Select("SELECT id, title, description, type, theme, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM story_scene WHERE type = #{type} ORDER BY id DESC")
    List<StoryScene> findByType(String type);
    
    @Select("SELECT id, title, description, type, theme, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM story_scene WHERE theme = #{theme} ORDER BY id DESC")
    List<StoryScene> findByTheme(String theme);
    
    @Select("SELECT id, title, description, type, theme, age_range as ageRange, image_url as imageUrl, create_time as createTime, update_time as updateTime FROM story_scene WHERE age_range LIKE CONCAT('%', #{age}, '%') ORDER BY id DESC")
    List<StoryScene> findByAge(Integer age);
    
    @Insert("INSERT INTO story_scene(title, description, type, theme, age_range, image_url) VALUES(#{title}, #{description}, #{type}, #{theme}, #{ageRange}, #{imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StoryScene storyScene);
    
    @Update("UPDATE story_scene SET title = #{title}, description = #{description}, type = #{type}, theme = #{theme}, age_range = #{ageRange}, image_url = #{imageUrl} WHERE id = #{id}")
    int update(StoryScene storyScene);
    
    @Delete("DELETE FROM story_scene WHERE id = #{id}")
    int delete(Integer id);
} 
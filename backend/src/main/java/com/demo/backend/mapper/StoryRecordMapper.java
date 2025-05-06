package com.demo.backend.mapper;

import com.demo.backend.entity.StoryRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoryRecordMapper {
    
    @Select("SELECT id, child_id as childId, story_type as storyType, story_theme as storyTheme, duration, create_time as createTime FROM story_record WHERE child_id = #{childId} ORDER BY create_time DESC")
    List<StoryRecord> findByChildId(Integer childId);
    
    @Select("SELECT id, child_id as childId, story_type as storyType, story_theme as storyTheme, duration, create_time as createTime FROM story_record WHERE id = #{id}")
    StoryRecord findById(Integer id);
    
    @Insert("INSERT INTO story_record(child_id, story_type, story_theme, duration) VALUES(#{childId}, #{storyType}, #{storyTheme}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StoryRecord storyRecord);
    
    @Update("UPDATE story_record SET duration = #{duration} WHERE id = #{id}")
    int updateDuration(@Param("id") Integer id, @Param("duration") Integer duration);
    
    @Delete("DELETE FROM story_record WHERE id = #{id}")
    int delete(Integer id);
    
    @Select("SELECT id, child_id as childId, story_type as storyType, story_theme as storyTheme, duration, create_time as createTime FROM story_record WHERE child_id = #{childId} AND story_type = #{storyType} ORDER BY create_time DESC")
    List<StoryRecord> findByChildIdAndType(@Param("childId") Integer childId, @Param("storyType") String storyType);
    
    @Select("<script>" +
            "SELECT id, child_id as childId, story_type as storyType, story_theme as storyTheme, duration, create_time as createTime " +
            "FROM story_record " +
            "WHERE child_id IN " +
            "<foreach collection='childIds' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "<if test='storyType != null and storyType != \"\"'> AND story_type = #{storyType}</if>" +
            "<if test='startDate != null and startDate != \"\"'> AND DATE(create_time) &gt;= #{startDate}</if>" +
            "<if test='endDate != null and endDate != \"\"'> AND DATE(create_time) &lt;= #{endDate}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<StoryRecord> findByChildIds(@Param("childIds") List<Integer> childIds, 
                                     @Param("storyType") String storyType, 
                                     @Param("startDate") String startDate, 
                                     @Param("endDate") String endDate);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM story_record " +
            "WHERE child_id IN " +
            "<foreach collection='childIds' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    int countByChildIds(@Param("childIds") List<Integer> childIds);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM story_record " +
            "WHERE child_id IN " +
            "<foreach collection='childIds' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            " AND story_type = #{storyType}" +
            "</script>")
    int countByChildIdsAndType(@Param("childIds") List<Integer> childIds, @Param("storyType") String storyType);
} 
package com.demo.backend.mapper;

import com.demo.backend.entity.StoryContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoryContentMapper {
    
    @Select("SELECT id, record_id as recordId, content, speaker, sequence, create_time as createTime FROM story_content WHERE id = #{id}")
    StoryContent findById(Integer id);
    
    @Select("SELECT id, record_id as recordId, content, speaker, sequence, create_time as createTime FROM story_content WHERE record_id = #{recordId} ORDER BY sequence")
    List<StoryContent> findByRecordId(Integer recordId);
    
    @Insert("INSERT INTO story_content(record_id, content, speaker, sequence) VALUES(#{recordId}, #{content}, #{speaker}, #{sequence})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StoryContent storyContent);
    
    @Delete("DELETE FROM story_content WHERE record_id = #{recordId}")
    int deleteByRecordId(Integer recordId);
    
    @Select("SELECT MAX(sequence) FROM story_content WHERE record_id = #{recordId}")
    Integer getMaxSequence(Integer recordId);
} 
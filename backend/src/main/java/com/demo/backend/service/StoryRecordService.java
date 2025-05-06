package com.demo.backend.service;

import com.demo.backend.entity.StoryRecord;
import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.Child;

import java.util.List;

public interface StoryRecordService {
    
    /**
     * 根据孩子ID查询故事记录
     * @param childId 孩子ID
     * @return 故事记录列表
     */
    List<StoryRecord> findByChildId(Integer childId);
    
    /**
     * 根据ID查询故事记录
     * @param id 故事记录ID
     * @return 故事记录
     */
    StoryRecord findById(Integer id);
    
    /**
     * 创建故事记录
     * @param storyRecord 故事记录
     * @return 创建后的故事记录
     */
    StoryRecord createStoryRecord(StoryRecord storyRecord);
    
    /**
     * 更新故事时长
     * @param id 故事记录ID
     * @param duration 时长（秒）
     * @return 是否更新成功
     */
    boolean updateDuration(Integer id, Integer duration);
    
    /**
     * 删除故事记录
     * @param id 故事记录ID
     * @return 是否删除成功
     */
    boolean deleteStoryRecord(Integer id);
    
    /**
     * 添加故事内容
     * @param storyContent 故事内容
     * @return 添加后的故事内容
     */
    StoryContent addStoryContent(StoryContent storyContent);
    
    /**
     * 根据故事记录ID查询内容
     * @param recordId 故事记录ID
     * @return 故事内容列表
     */
    List<StoryContent> findContentByRecordId(Integer recordId);
    
    /**
     * 根据孩子ID和故事类型查询记录
     * @param childId 孩子ID
     * @param storyType 故事类型
     * @return 故事记录列表
     */
    List<StoryRecord> findByChildIdAndType(Integer childId, String storyType);
    
    /**
     * 根据用户ID查询孩子列表
     * @param userId 用户ID
     * @return 孩子列表
     */
    List<Child> findChildrenByUserId(Integer userId);
    
    /**
     * 创建孩子记录
     * @param child 孩子信息
     * @return 创建后的孩子记录
     */
    Child createChild(Child child);
} 
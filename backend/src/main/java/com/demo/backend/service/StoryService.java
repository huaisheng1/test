package com.demo.backend.service;

import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;

import java.util.List;
import java.util.Map;

public interface StoryService {

    /**
     * 保存故事记录和内容
     * @param storyRecord 故事记录
     * @param contentList 故事内容列表
     * @return 保存的故事记录
     */
    StoryRecord saveStoryRecord(StoryRecord storyRecord, List<StoryContent> contentList);
    
    /**
     * 根据用户ID查询故事记录
     * @param userId 用户ID
     * @param storyType 故事类型（可选）
     * @param startDate 开始日期（可选，格式：YYYY-MM-DD）
     * @param endDate 结束日期（可选，格式：YYYY-MM-DD）
     * @param limit 返回记录数限制（可选）
     * @return 故事记录列表
     */
    List<Map<String, Object>> findStoryRecordsByUserId(Integer userId, String storyType, String startDate, String endDate, Integer limit);
    
    /**
     * 根据ID查询故事记录详情
     * @param recordId 记录ID
     * @return 故事记录详情
     */
    StoryRecord findStoryRecordById(Integer recordId);
    
    /**
     * 统计指定孩子的故事记录总数
     * @param childIds 孩子ID列表
     * @return 故事记录总数
     */
    int countStoryRecordsByChildIds(List<Integer> childIds);
    
    /**
     * 统计指定孩子特定类型的故事记录数
     * @param childIds 孩子ID列表
     * @param storyType 故事类型
     * @return 故事记录数
     */
    int countStoryRecordsByChildIdsAndType(List<Integer> childIds, String storyType);
} 
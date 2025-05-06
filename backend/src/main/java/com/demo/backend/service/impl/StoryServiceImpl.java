package com.demo.backend.service.impl;

import com.demo.backend.entity.Child;
import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;
import com.demo.backend.mapper.ChildMapper;
import com.demo.backend.mapper.StoryContentMapper;
import com.demo.backend.mapper.StoryRecordMapper;
import com.demo.backend.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRecordMapper storyRecordMapper;
    
    @Autowired
    private StoryContentMapper storyContentMapper;
    
    @Autowired
    private ChildMapper childMapper;
    
    @Override
    @Transactional
    public StoryRecord saveStoryRecord(StoryRecord storyRecord, List<StoryContent> contentList) {
        // 插入故事记录
        storyRecordMapper.insert(storyRecord);
        
        // 插入故事内容
        if (contentList != null && !contentList.isEmpty()) {
            for (StoryContent content : contentList) {
                content.setRecordId(storyRecord.getId());
                storyContentMapper.insert(content);
            }
        }
        
        // 返回完整的故事记录信息
        return findStoryRecordById(storyRecord.getId());
    }
    
    @Override
    public List<Map<String, Object>> findStoryRecordsByUserId(Integer userId, String storyType, String startDate, String endDate, Integer limit) {
        // 获取用户所有孩子的ID
        List<Child> children = childMapper.findByUserId(userId);
        List<Integer> childIds = new ArrayList<>();
        
        if (children != null && !children.isEmpty()) {
            for (Child child : children) {
                childIds.add(child.getId());
            }
        }
        
        if (childIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询故事记录
        List<StoryRecord> records;
        try {
            records = storyRecordMapper.findByChildIds(childIds, storyType, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        
        // 转换为前端所需的格式
        List<Map<String, Object>> result = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            for (StoryRecord record : records) {
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.put("id", record.getId());
                recordMap.put("storyType", record.getStoryType());
                recordMap.put("storyTheme", record.getStoryTheme());
                recordMap.put("duration", record.getDuration());
                recordMap.put("createTime", record.getCreateTime());
                
                // 获取孩子信息
                Child child = childMapper.findById(record.getChildId());
                if (child != null) {
                    recordMap.put("childName", child.getName());
                    recordMap.put("childAge", child.getAge());
                }
                
                result.add(recordMap);
                
                // 如果有限制，并且已经达到限制数量，就停止添加
                if (limit != null && result.size() >= limit) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    @Override
    public StoryRecord findStoryRecordById(Integer recordId) {
        // 查询故事记录
        StoryRecord record = storyRecordMapper.findById(recordId);
        
        if (record != null) {
            // 查询关联的孩子信息
            Child child = childMapper.findById(record.getChildId());
            record.setChild(child);
            
            // 查询关联的故事内容
            List<StoryContent> contentList = storyContentMapper.findByRecordId(recordId);
            record.setContentList(contentList);
        }
        
        return record;
    }
    
    @Override
    public int countStoryRecordsByChildIds(List<Integer> childIds) {
        if (childIds == null || childIds.isEmpty()) {
            return 0;
        }
        return storyRecordMapper.countByChildIds(childIds);
    }
    
    @Override
    public int countStoryRecordsByChildIdsAndType(List<Integer> childIds, String storyType) {
        if (childIds == null || childIds.isEmpty() || storyType == null || storyType.isEmpty()) {
            return 0;
        }
        return storyRecordMapper.countByChildIdsAndType(childIds, storyType);
    }
} 
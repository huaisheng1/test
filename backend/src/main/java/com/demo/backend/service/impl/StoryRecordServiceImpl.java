package com.demo.backend.service.impl;

import com.demo.backend.entity.Child;
import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;
import com.demo.backend.mapper.ChildMapper;
import com.demo.backend.mapper.StoryContentMapper;
import com.demo.backend.mapper.StoryRecordMapper;
import com.demo.backend.service.StoryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoryRecordServiceImpl implements StoryRecordService {

    @Autowired
    private StoryRecordMapper storyRecordMapper;
    
    @Autowired
    private StoryContentMapper storyContentMapper;
    
    @Autowired
    private ChildMapper childMapper;
    
    @Override
    public List<StoryRecord> findByChildId(Integer childId) {
        List<StoryRecord> records = storyRecordMapper.findByChildId(childId);
        // 加载关联的孩子信息和故事内容
        records.forEach(this::loadAssociatedData);
        return records;
    }
    
    @Override
    public StoryRecord findById(Integer id) {
        StoryRecord record = storyRecordMapper.findById(id);
        if (record != null) {
            loadAssociatedData(record);
        }
        return record;
    }
    
    @Override
    @Transactional
    public StoryRecord createStoryRecord(StoryRecord storyRecord) {
        // 检查孩子是否存在
        Child child = childMapper.findById(storyRecord.getChildId());
        if (child == null) {
            return null;
        }
        
        // 插入故事记录
        storyRecordMapper.insert(storyRecord);
        
        // 如果有故事内容，也一起插入
        if (storyRecord.getContentList() != null && !storyRecord.getContentList().isEmpty()) {
            for (StoryContent content : storyRecord.getContentList()) {
                content.setRecordId(storyRecord.getId());
                storyContentMapper.insert(content);
            }
        }
        
        // 查询完整的故事记录
        StoryRecord createdRecord = storyRecordMapper.findById(storyRecord.getId());
        if (createdRecord != null) {
            loadAssociatedData(createdRecord);
        }
        
        return createdRecord;
    }
    
    @Override
    public boolean updateDuration(Integer id, Integer duration) {
        // 检查记录是否存在
        StoryRecord record = storyRecordMapper.findById(id);
        if (record == null) {
            return false;
        }
        
        // 更新时长
        int result = storyRecordMapper.updateDuration(id, duration);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteStoryRecord(Integer id) {
        // 检查记录是否存在
        StoryRecord record = storyRecordMapper.findById(id);
        if (record == null) {
            return false;
        }
        
        // 先删除相关的故事内容
        storyContentMapper.deleteByRecordId(id);
        
        // 再删除故事记录
        int result = storyRecordMapper.delete(id);
        return result > 0;
    }
    
    @Override
    public StoryContent addStoryContent(StoryContent storyContent) {
        // 检查故事记录是否存在
        StoryRecord record = storyRecordMapper.findById(storyContent.getRecordId());
        if (record == null) {
            return null;
        }
        
        // 如果序号为空，则自动获取最大序号+1
        if (storyContent.getSequence() == null) {
            Integer maxSequence = storyContentMapper.getMaxSequence(storyContent.getRecordId());
            storyContent.setSequence(maxSequence == null ? 1 : maxSequence + 1);
        }
        
        // 插入故事内容
        storyContentMapper.insert(storyContent);
        return storyContent;
    }
    
    @Override
    public List<StoryContent> findContentByRecordId(Integer recordId) {
        return storyContentMapper.findByRecordId(recordId);
    }
    
    @Override
    public List<StoryRecord> findByChildIdAndType(Integer childId, String storyType) {
        List<StoryRecord> records = storyRecordMapper.findByChildIdAndType(childId, storyType);
        records.forEach(this::loadAssociatedData);
        return records;
    }
    
    @Override
    public List<Child> findChildrenByUserId(Integer userId) {
        return childMapper.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public Child createChild(Child child) {
        // 检查用户是否存在
        if (child.getUserId() == null) {
            return null;
        }
        
        // 插入孩子记录
        childMapper.insert(child);
        
        // 查询完整的孩子信息
        return childMapper.findById(child.getId());
    }
    
    /**
     * 加载记录关联的数据（孩子信息和故事内容）
     */
    private void loadAssociatedData(StoryRecord record) {
        // 加载孩子信息
        Child child = childMapper.findById(record.getChildId());
        if (child != null) {
            record.setChild(child);
        }
        
        // 加载故事内容
        List<StoryContent> contentList = storyContentMapper.findByRecordId(record.getId());
        if (contentList != null && !contentList.isEmpty()) {
            record.setContentList(contentList);
        }
    }
} 
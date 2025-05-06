package com.demo.backend.controller;

import com.demo.backend.entity.Child;
import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;
import com.demo.backend.service.ChildService;
import com.demo.backend.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/story")
@CrossOrigin
public class StoryController {

    @Autowired
    private StoryService storyService;
    
    @Autowired
    private ChildService childService;

    @PostMapping("/save-history")
    public ResponseEntity<Map<String, Object>> saveStoryHistory(@RequestBody Map<String, Object> storyData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 解析请求数据
            Integer userId = (Integer) storyData.get("userId");
            String childName = (String) storyData.get("childName");
            Integer childAge = (Integer) storyData.get("childAge");
            String storyType = (String) storyData.get("storyType");
            String storyTheme = (String) storyData.get("storyTheme");
            Integer duration = (Integer) storyData.get("duration");
            
            // 检查用户是否有对应的孩子记录
            List<Child> children = childService.findByUserId(userId);
            Child child = null;
            
            // 查找匹配的孩子记录或创建新记录
            if (children != null && !children.isEmpty()) {
                for (Child c : children) {
                    if (c.getName().equals(childName) && c.getAge().equals(childAge)) {
                        child = c;
                        break;
                    }
                }
            }
            
            // 如果没找到对应的孩子，创建一个新的孩子记录
            if (child == null) {
                child = new Child();
                child.setUserId(userId);
                child.setName(childName);
                child.setAge(childAge);
                child = childService.addChild(child);
            }
            
            // 创建故事记录
            StoryRecord storyRecord = new StoryRecord();
            storyRecord.setChildId(child.getId());
            storyRecord.setStoryType(storyType);
            storyRecord.setStoryTheme(storyTheme);
            storyRecord.setDuration(duration);
            
            // 解析消息列表
            List<Map<String, Object>> messagesData = (List<Map<String, Object>>) storyData.get("messages");
            List<StoryContent> contentList = new ArrayList<>();
            
            if (messagesData != null && !messagesData.isEmpty()) {
                for (Map<String, Object> message : messagesData) {
                    StoryContent content = new StoryContent();
                    content.setContent((String) message.get("content"));
                    content.setSpeaker((String) message.get("speaker"));
                    content.setSequence((Integer) message.get("sequence"));
                    contentList.add(content);
                }
            }
            
            // 保存故事记录和内容
            StoryRecord savedRecord = storyService.saveStoryRecord(storyRecord, contentList);
            
            response.put("code", 200);
            response.put("message", "故事历史记录保存成功");
            response.put("data", savedRecord);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "故事历史记录保存失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getStoryHistory(
            @RequestParam Integer userId,
            @RequestParam(required = false) String storyType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查询故事记录
            List<Map<String, Object>> records = storyService.findStoryRecordsByUserId(userId, storyType, startDate, endDate, limit);
            
            response.put("code", 200);
            response.put("message", "获取故事历史记录成功");
            response.put("data", records);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "获取故事历史记录失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/detail/{recordId}")
    public ResponseEntity<Map<String, Object>> getStoryDetail(@PathVariable Integer recordId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查询故事详情
            StoryRecord record = storyService.findStoryRecordById(recordId);
            
            if (record != null) {
                response.put("code", 200);
                response.put("message", "获取故事详情成功");
                response.put("data", record);
            } else {
                response.put("code", 404);
                response.put("message", "故事记录不存在");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "获取故事详情失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStoryStats(@RequestParam Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取用户所有孩子的ID
            List<Child> children = childService.findByUserId(userId);
            List<Integer> childIds = new ArrayList<>();
            
            if (children != null && !children.isEmpty()) {
                for (Child child : children) {
                    childIds.add(child.getId());
                }
            }
            
            // 统计数据
            Map<String, Object> stats = new HashMap<>();
            
            if (!childIds.isEmpty()) {
                // 查询总故事数
                int totalCount = storyService.countStoryRecordsByChildIds(childIds);
                stats.put("count", totalCount);
                
                // 查询各类型故事数
                int educationCount = storyService.countStoryRecordsByChildIdsAndType(childIds, "生活教育");
                int learningCount = storyService.countStoryRecordsByChildIdsAndType(childIds, "学习成长");
                
                stats.put("educationCount", educationCount);
                stats.put("learningCount", learningCount);
                
                // 如果需要，可以添加更多统计数据...
            } else {
                stats.put("count", 0);
                stats.put("educationCount", 0);
                stats.put("learningCount", 0);
            }
            
            response.put("code", 200);
            response.put("message", "获取故事统计数据成功");
            response.put("data", stats);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "获取故事统计数据失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
} 
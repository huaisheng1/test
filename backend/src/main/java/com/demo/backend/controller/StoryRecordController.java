package com.demo.backend.controller;

import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;
import com.demo.backend.service.StoryRecordService;
import com.demo.backend.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/story-record")
@CrossOrigin
public class StoryRecordController {

    @Autowired
    private StoryRecordService storyRecordService;
    
    @Autowired
    private StoryService storyService;
    
    @GetMapping("/child/{childId}")
    public ResponseEntity<Map<String, Object>> getRecordsByChildId(@PathVariable Integer childId) {
        List<StoryRecord> records = storyRecordService.findByChildId(childId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取故事记录成功");
        response.put("data", records);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRecordById(@PathVariable Integer id) {
        StoryRecord record = storyRecordService.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (record != null) {
            response.put("code", 200);
            response.put("message", "获取故事记录成功");
            response.put("data", record);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "故事记录不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getRecordsByUserId(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> records = storyService.findStoryRecordsByUserId(userId, null, null, null, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取故事记录成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取故事记录失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRecord(@RequestBody StoryRecord storyRecord) {
        StoryRecord createdRecord = storyRecordService.createStoryRecord(storyRecord);
        
        Map<String, Object> response = new HashMap<>();
        if (createdRecord != null) {
            response.put("code", 200);
            response.put("message", "创建故事记录成功");
            response.put("data", createdRecord);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "创建故事记录失败，孩子信息不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PutMapping("/duration/{id}")
    public ResponseEntity<Map<String, Object>> updateDuration(@PathVariable Integer id, @RequestBody Map<String, Integer> durationMap) {
        Integer duration = durationMap.get("duration");
        boolean success = storyRecordService.updateDuration(id, duration);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "更新故事时长成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "故事记录不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecord(@PathVariable Integer id) {
        boolean success = storyRecordService.deleteStoryRecord(id);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "删除故事记录成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "故事记录不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/content/add")
    public ResponseEntity<Map<String, Object>> addContent(@RequestBody StoryContent storyContent) {
        StoryContent addedContent = storyRecordService.addStoryContent(storyContent);
        
        Map<String, Object> response = new HashMap<>();
        if (addedContent != null) {
            response.put("code", 200);
            response.put("message", "添加故事内容成功");
            response.put("data", addedContent);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "添加故事内容失败，故事记录不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/content/{recordId}")
    public ResponseEntity<Map<String, Object>> getContentByRecordId(@PathVariable Integer recordId) {
        List<StoryContent> contentList = storyRecordService.findContentByRecordId(recordId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取故事内容成功");
        response.put("data", contentList);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/child/{childId}/type/{storyType}")
    public ResponseEntity<Map<String, Object>> getRecordsByChildIdAndType(
            @PathVariable Integer childId, 
            @PathVariable String storyType) {
        List<StoryRecord> records = storyRecordService.findByChildIdAndType(childId, storyType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取故事记录成功");
        response.put("data", records);
        return ResponseEntity.ok(response);
    }
} 
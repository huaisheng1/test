package com.demo.backend.controller;

import com.demo.backend.entity.Child;
import com.demo.backend.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/child")
@CrossOrigin
public class ChildController {

    @Autowired
    private ChildService childService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getChildrenByUserId(@PathVariable Integer userId) {
        List<Child> children = childService.findByUserId(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取孩子列表成功");
        response.put("data", children);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChildById(@PathVariable Integer id) {
        Child child = childService.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (child != null) {
            response.put("code", 200);
            response.put("message", "获取孩子信息成功");
            response.put("data", child);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "孩子信息不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addChild(@RequestBody Child child) {
        Child addedChild = childService.addChild(child);
        
        Map<String, Object> response = new HashMap<>();
        if (addedChild != null) {
            response.put("code", 200);
            response.put("message", "添加孩子信息成功");
            response.put("data", addedChild);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "添加孩子信息失败，用户不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateChild(@PathVariable Integer id, @RequestBody Child child) {
        child.setId(id);
        Child updatedChild = childService.updateChild(child);
        
        Map<String, Object> response = new HashMap<>();
        if (updatedChild != null) {
            response.put("code", 200);
            response.put("message", "更新孩子信息成功");
            response.put("data", updatedChild);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "孩子信息不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChild(@PathVariable Integer id) {
        boolean success = childService.deleteChild(id);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "删除孩子信息成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "孩子信息不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/count/{userId}")
    public ResponseEntity<Map<String, Object>> countByUserId(@PathVariable Integer userId) {
        int count = childService.countByUserId(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取孩子数量成功");
        response.put("data", count);
        return ResponseEntity.ok(response);
    }
} 
package com.demo.backend.controller;

import com.demo.backend.entity.User;
import com.demo.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        
        User user = userService.login(username, password);
        
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        
        Map<String, Object> response = new HashMap<>();
        if (registeredUser != null) {
            response.put("code", 200);
            response.put("message", "注册成功");
            response.put("data", registeredUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "注册失败，用户名已存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> resetForm) {
        String username = resetForm.get("username");
        String email = resetForm.get("email");
        String newPassword = resetForm.get("password");
        
        boolean success = userService.resetPassword(username, email, newPassword);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "密码重置成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "密码重置失败，用户名或邮箱不匹配");
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取用户列表成功");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        User user = userService.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("code", 200);
            response.put("message", "获取用户信息成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        
        Map<String, Object> response = new HashMap<>();
        if (updatedUser != null) {
            response.put("code", 200);
            response.put("message", "更新用户信息成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        boolean success = userService.deleteUser(id);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "删除用户成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }
    }
}
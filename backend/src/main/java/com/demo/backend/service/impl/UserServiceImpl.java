package com.demo.backend.service.impl;

import com.demo.backend.entity.User;
import com.demo.backend.mapper.UserMapper;
import com.demo.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // 密码处理，返回用户信息时不返回密码
            user.setPassword(null);
            return user;
        }
        return null;
    }
    
    @Override
    public User register(User user) {
        // 检查用户是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return null;
        }
        
        // 如果没有设置角色，默认为user
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("user");
        }
        
        // 插入新用户
        userMapper.insert(user);
        
        // 查询完整的用户信息
        User registeredUser = userMapper.findById(user.getId());
        if (registeredUser != null) {
            // 返回用户信息时不返回密码
            registeredUser.setPassword(null);
        }
        
        return registeredUser;
    }
    
    @Override
    public boolean resetPassword(String username, String email, String newPassword) {
        // 先验证账号和邮箱是否匹配
        User user = userMapper.findByUsername(username);
        if (user == null || !email.equals(user.getEmail())) {
            return false;
        }
        
        // 更新密码
        int result = userMapper.updatePassword(username, email, newPassword);
        return result > 0;
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = userMapper.findAll();
        // 清除密码
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    @Override
    public User findById(Integer id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    @Override
    public User updateUser(User user) {
        // 先查找用户是否存在
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            return null;
        }
        
        // 更新用户信息
        userMapper.update(user);
        
        // 返回更新后的用户信息
        User updatedUser = userMapper.findById(user.getId());
        if (updatedUser != null) {
            updatedUser.setPassword(null);
        }
        return updatedUser;
    }
    
    @Override
    public boolean deleteUser(Integer id) {
        // 先查找用户是否存在
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            return false;
        }
        
        // 删除用户
        int result = userMapper.delete(id);
        return result > 0;
    }
} 
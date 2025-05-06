package com.demo.backend.service.impl;

import com.demo.backend.entity.Child;
import com.demo.backend.entity.User;
import com.demo.backend.mapper.ChildMapper;
import com.demo.backend.mapper.UserMapper;
import com.demo.backend.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildMapper childMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<Child> findByUserId(Integer userId) {
        return childMapper.findByUserId(userId);
    }
    
    @Override
    public Child findById(Integer id) {
        Child child = childMapper.findById(id);
        if (child != null) {
            // 加载关联的用户信息
            User user = userMapper.findById(child.getUserId());
            if (user != null) {
                user.setPassword(null);
                child.setUser(user);
            }
        }
        return child;
    }
    
    @Override
    public Child addChild(Child child) {
        // 检查用户是否存在
        User user = userMapper.findById(child.getUserId());
        if (user == null) {
            return null;
        }
        
        // 插入孩子信息
        childMapper.insert(child);
        
        // 查询完整的孩子信息
        Child addedChild = childMapper.findById(child.getId());
        if (addedChild != null) {
            // 设置关联的用户信息
            user.setPassword(null);
            addedChild.setUser(user);
        }
        
        return addedChild;
    }
    
    @Override
    public Child updateChild(Child child) {
        // 检查孩子是否存在
        Child existingChild = childMapper.findById(child.getId());
        if (existingChild == null) {
            return null;
        }
        
        // 更新孩子信息
        childMapper.update(child);
        
        // 查询更新后的孩子信息
        Child updatedChild = childMapper.findById(child.getId());
        if (updatedChild != null) {
            // 加载关联的用户信息
            User user = userMapper.findById(updatedChild.getUserId());
            if (user != null) {
                user.setPassword(null);
                updatedChild.setUser(user);
            }
        }
        
        return updatedChild;
    }
    
    @Override
    public boolean deleteChild(Integer id) {
        // 检查孩子是否存在
        Child existingChild = childMapper.findById(id);
        if (existingChild == null) {
            return false;
        }
        
        // 删除孩子信息
        int result = childMapper.delete(id);
        return result > 0;
    }
    
    @Override
    public int countByUserId(Integer userId) {
        return childMapper.countByUserId(userId);
    }
} 
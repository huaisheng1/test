package com.demo.backend.service;

import com.demo.backend.entity.User;

import java.util.List;

public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    User register(User user);
    
    /**
     * 忘记密码，重置密码
     * @param username 用户名
     * @param email 邮箱
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String username, String email, String newPassword);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Integer id);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Integer id);
} 
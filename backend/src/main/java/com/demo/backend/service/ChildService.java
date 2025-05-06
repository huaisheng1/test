package com.demo.backend.service;

import com.demo.backend.entity.Child;

import java.util.List;

public interface ChildService {
    
    /**
     * 根据用户ID查询孩子列表
     * @param userId 用户ID
     * @return 孩子列表
     */
    List<Child> findByUserId(Integer userId);
    
    /**
     * 根据ID查询孩子信息
     * @param id 孩子ID
     * @return 孩子信息
     */
    Child findById(Integer id);
    
    /**
     * 添加孩子信息
     * @param child 孩子信息
     * @return 添加后的孩子信息
     */
    Child addChild(Child child);
    
    /**
     * 更新孩子信息
     * @param child 孩子信息
     * @return 更新后的孩子信息
     */
    Child updateChild(Child child);
    
    /**
     * 删除孩子信息
     * @param id 孩子ID
     * @return 是否删除成功
     */
    boolean deleteChild(Integer id);
    
    /**
     * 查询用户的孩子数量
     * @param userId 用户ID
     * @return 孩子数量
     */
    int countByUserId(Integer userId);
} 
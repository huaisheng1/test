package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Child {
    private Integer id;
    private Integer userId;
    private String name;
    private Integer age;
    private String gender;
    private String avatar;
    private Date createTime;
    private Date updateTime;
    
    // 关联的用户信息（非数据库字段）
    private User user;
} 
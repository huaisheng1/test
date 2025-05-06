package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private Date createTime;
    private Date updateTime;
    
    // 关联的孩子列表（非数据库字段）
    private List<Child> children;
} 
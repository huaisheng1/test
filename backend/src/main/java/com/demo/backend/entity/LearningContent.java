package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LearningContent {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String ageRange;
    private String imageUrl;
    private Date createTime;
    private Date updateTime;
} 
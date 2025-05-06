package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StoryScene {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String theme;
    private String ageRange;
    private String imageUrl;
    private Date createTime;
    private Date updateTime;
} 
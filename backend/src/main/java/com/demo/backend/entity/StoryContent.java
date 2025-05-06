package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StoryContent {
    private Integer id;
    private Integer recordId;
    private String content;
    private String speaker;
    private Integer sequence;
    private Date createTime;
    
    // 关联的故事记录（非数据库字段）
    private StoryRecord storyRecord;
} 
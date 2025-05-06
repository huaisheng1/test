package com.demo.backend.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StoryRecord {
    private Integer id;
    private Integer childId;
    private String storyType;
    private String storyTheme;
    private Integer duration;
    private Date createTime;
    
    // 关联的儿童信息（非数据库字段）
    private Child child;
    
    // 关联的故事内容列表（非数据库字段）
    private List<StoryContent> contentList;
} 
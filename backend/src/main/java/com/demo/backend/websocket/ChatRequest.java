package com.demo.backend.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 故事互动请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    /**
     * 用户名（孩子的名字）
     */
    private String username;

    /**
     * 消息文本（孩子的输入）
     */
    private String text;

    /**
     * 消息类型 (例如: story, connect, disconnect)
     */
    private String type;

    /**
     * 用户ID，用于追踪会话
     */
    private String userId;
    
    /**
     * 是否是新故事开始
     */
    @JsonProperty("isNewStory")
    private boolean newStory;
    
    /**
     * 孩子的年龄
     */
    private int childAge;
    
    /**
     * 故事类型（生活教育/学习成长）
     */
    private String storyType;
    
    /**
     * 故事主题
     */
    private String storyTheme;
    
    /**
     * 故事开始时间戳
     */
    private Long startTime;
    
    /**
     * 为了保持与现有代码的兼容性，提供isNewStory方法
     * @return 是否是新故事开始
     */
    public boolean isNewStory() {
        return newStory;
    }
    
    /**
     * 为了保持与现有代码的兼容性，提供setNewStory方法
     * @param newStory 是否是新故事开始
     */
    public void setIsNewStory(boolean newStory) {
        this.newStory = newStory;
    }
}

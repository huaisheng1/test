/**
 * @projectName childrenAI
 * @package com.demo.backend.websocket
 * @className com.demo.backend.websocket.WebSocketConfig
 * @copyright Copyright 2024 Thunisoft, Inc All rights reserved.
 */
package com.demo.backend.websocket;

import com.demo.backend.service.StoryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * WebSocket配置类
 * 用于儿童互动故事书系统中的AI故事生成功能
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final wsOpenAiChatModel openAiChatModel;
    private final StoryRecordService storyRecordService;

    @Autowired
    public WebSocketConfig(wsOpenAiChatModel openAiChatModel,  StoryRecordService storyRecordService) {
        this.openAiChatModel = openAiChatModel;
        this.storyRecordService = storyRecordService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(openAiChatModel, storyRecordService), "/ws/story")
                .setAllowedOrigins("*") // 允许所有源，实际生产环境应限制源
                .addInterceptors(new HttpSessionHandshakeInterceptor()); // 添加 HTTP 会话拦截器，用于传递 HTTP 会话属性到 WebSocket 会话
    }
}

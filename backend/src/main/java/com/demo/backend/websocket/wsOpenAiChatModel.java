/**
 * @projectName childrenAI
 * @package com.demo.backend.websocket
 * @className com.demo.backend.websocket.wsOpenAiChatModel
 * @copyright Copyright 2024 Thunisoft, Inc All rights reserved.
 */
package com.demo.backend.websocket;


import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


@Service
public class wsOpenAiChatModel {
    @Autowired
    private OpenAiChatModel openAiChatModel;
    // 接收消息msg并返回流式数据
    public Flux<String> stream(String msg) {
        // 将msg传递给openAiChatModel并返回流式数据
        return openAiChatModel.stream(msg);
    }
}

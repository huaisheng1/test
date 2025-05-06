package com.demo.backend.websocket;


import com.demo.backend.entity.Child;
import com.demo.backend.entity.StoryContent;
import com.demo.backend.entity.StoryRecord;
import com.demo.backend.service.StoryRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final wsOpenAiChatModel openAiChatModel;
    private StoryRecordService  storyRecordService;

    // 存储所有活跃的WebSocket会话
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    // 存储每个会话的故事上下文
    private static final Map<String, StoryContext> STORY_CONTEXTS = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(wsOpenAiChatModel openAiChatModel,  StoryRecordService storyRecordService) {
        this.openAiChatModel = openAiChatModel;
        this.storyRecordService = storyRecordService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 生成唯一会话ID
        String sessionId = session.getId();
        SESSIONS.put(sessionId, session);
        // 创建新的故事上下文
        STORY_CONTEXTS.put(sessionId, new StoryContext());
        log.info("新的故事互动WebSocket连接已建立: {}", sessionId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除会话和故事上下文
        String sessionId = session.getId();
        SESSIONS.remove(sessionId);
        STORY_CONTEXTS.remove(sessionId);
        log.info("故事互动WebSocket连接已关闭: {}, 状态: {}", sessionId, status);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 获取消息内容
        String payload = message.getPayload();
        log.info("收到故事互动WebSocket消息: {}", payload);

        try {
            // 解析用户消息
            ChatRequest request = parseMessage(payload);
            String userInput = request.getText();
            String username = request.getUsername();
            String userId = request.getUserId();
            String storyType = request.getStoryType();
            String storyTheme = request.getStoryTheme();

            log.info("用户 [{}] 发送互动: {}, 故事类型: {}, 主题: {}", username, userInput, storyType, storyTheme);

            // 获取会话ID
            String sessionId = session.getId();

            // 获取或创建故事上下文
            StoryContext storyContext = STORY_CONTEXTS.getOrDefault(sessionId, new StoryContext());
            
            // 记录故事ID，用于保存对话内容
            Integer storyRecordId = storyContext.getStoryRecordId();

            // 如果是新故事，初始化故事上下文和记录
            if (request.isNewStory()) {
                storyContext = new StoryContext();
                storyContext.setStoryType(storyType);
                storyContext.setChildAge(request.getChildAge());
                storyContext.setChildName(username);
                STORY_CONTEXTS.put(sessionId, storyContext);
                
                // 创建新的故事记录
                try {
                    // 获取childId
                    Integer childId = getChildIdFromUserId(userId, username, request.getChildAge());
                    if (childId != null) {
                        // 创建故事记录
                        StoryRecord storyRecord = new StoryRecord();
                        storyRecord.setChildId(childId);
                        storyRecord.setStoryType(storyType);
                        storyRecord.setStoryTheme(storyTheme != null ? storyTheme : "未知主题");
                        storyRecord.setDuration(0); // 初始时长为0
                        
                        // 保存故事记录
                        StoryRecord savedRecord = storyRecordService.createStoryRecord(storyRecord);
                        if (savedRecord != null) {
                            storyRecordId = savedRecord.getId();
                            storyContext.setStoryRecordId(storyRecordId);
                            log.info("创建新故事记录，ID: {}", storyRecordId);
                        }
                    }
                } catch (Exception e) {
                    log.error("创建故事记录失败", e);
                }
            }

            // 保存用户消息到故事内容表
            if (storyRecordId != null && !request.isNewStory()) {
                try {
                    StoryContent userContent = new StoryContent();
                    userContent.setRecordId(storyRecordId);
                    userContent.setContent(userInput);
                    userContent.setSpeaker("child");
                    // sequence将由service自动设置
                    storyRecordService.addStoryContent(userContent);
                    log.info("保存用户消息到故事内容表，故事ID: {}", storyRecordId);
                } catch (Exception e) {
                    log.error("保存用户消息失败", e);
                }
            }

            // 构建包含故事上下文的提示
            String prompt = buildStoryPrompt(userInput, storyContext);

            // 初始化累积响应的 StringBuilder
            StringBuilder responseBuilder = new StringBuilder();

            // 创建一个final引用的StoryContext对象，用于Lambda表达式
            final StoryContext finalStoryContext = storyContext;
            final Integer finalStoryRecordId = storyRecordId;

            // 使用 Flux<String> 流式返回数据给客户端
            Flux<String> aiResponse = openAiChatModel.stream(prompt+finalStoryContext)
                    .doOnNext(chunk -> {
                        if (!"[DONE]".equalsIgnoreCase(chunk)) {
                            responseBuilder.append(chunk); // 累加 AI 响应内容
                            // 更新故事上下文
                            finalStoryContext.addToStory(chunk);
                        }
                    })
                    .map(chunk -> "data:" + chunk + "\n\n")
                    .concatWith(Mono.just("data:[DONE]\n\n"));

            // 向客户端发送流式消息，同时监听完成事件
            aiResponse.subscribe(response -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(response));
                    }
                } catch (IOException e) {
                    log.error("发送故事消息失败", e);
                }
            }, error -> {
                // 异常处理
                log.error("处理故事互动时发生错误", error);
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage("data:抱歉，我们的故事魔法遇到了一点小问题。让我们重新开始吧！\n\n"));
                        session.sendMessage(new TextMessage("data:[DONE]\n\n"));
                    }
                } catch (IOException e) {
                    log.error("发送错误消息失败", e);
                }
            }, () -> {
                log.info("故事回复已完成，发送给小朋友 [{}]", username);
                
                // 保存AI回复到故事内容表
                if (finalStoryRecordId != null) {
                    try {
                        String aiResponseText = responseBuilder.toString();
                        StoryContent aiContent = new StoryContent();
                        aiContent.setRecordId(finalStoryRecordId);
                        aiContent.setContent(aiResponseText);
                        aiContent.setSpeaker("ai");
                        // sequence将由service自动设置
                        storyRecordService.addStoryContent(aiContent);
                        log.info("保存AI回复到故事内容表，故事ID: {}", finalStoryRecordId);
                        
                        // 更新故事时长
                        if (request.getStartTime() != null) {
                            long currentTime = System.currentTimeMillis();
                            long startTime = request.getStartTime();
                            int durationSeconds = (int) ((currentTime - startTime) / 1000);
                            
                            if (durationSeconds > 0) {
                                storyRecordService.updateDuration(finalStoryRecordId, durationSeconds);
                                log.info("更新故事时长: {} 秒, 故事ID: {}", durationSeconds, finalStoryRecordId);
                            }
                        }
                    } catch (Exception e) {
                        log.error("保存AI回复失败", e);
                    }
                }
                
                // 保存更新后的故事上下文
                STORY_CONTEXTS.put(sessionId, finalStoryContext);
            });
        } catch (Exception e) {
            log.error("处理故事互动WebSocket消息时发生错误", e);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("data:小朋友，我们遇到了一点小问题。让我们重新开始吧！\n\n"));
                session.sendMessage(new TextMessage("data:[DONE]\n\n"));
            }
        }
    }

    private String buildStoryPrompt(String userInput, StoryContext storyContext) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("\"你是一个专为3-6岁儿童设计的互动故事生成器。你的故事应该：\\n\" +\n" +
                "                \"1. 简单易懂，使用适合学龄前儿童的语言\\n\" +\n" +
                "                \"2. 富有教育意义，融入生活习惯或学习成长的内容\\n\" +\n" +
                "                \"3. 故事情节生动有趣，可以激发孩子的想象力\\n\" +\n" +
                "                \"4. 在关键处提供互动选择，让孩子参与故事发展\\n\" +\n" +
                "                \"5. 响应孩子的输入，调整故事走向\\n\" +\n" +
                "                \"6. 避免任何可能吓到孩子的内容\"));");

        if (storyContext.isEmpty()) {
            // 新故事开始的提示
            promptBuilder.append("你是一个专为3-6岁儿童设计的互动故事生成器。");
            promptBuilder.append("请为一个").append(storyContext.getChildAge()).append("岁的小朋友");
            promptBuilder.append("名叫").append(storyContext.getChildName()).append("创作一个互动故事。");

            if ("生活教育".equals(storyContext.getStoryType())) {
                promptBuilder.append("故事应该包含生活教育内容，如饮食卫生、作息规律、生活自理或安全自我保护。");
            } else if ("学习成长".equals(storyContext.getStoryType())) {
                promptBuilder.append("故事应该包含学习成长内容，如阅读、交流表达、益智教育、思维锻炼或艺术启发。");
            }

            promptBuilder.append("故事应该有生动有趣的情节，并在关键处给孩子选择，让他们参与到故事中。");
            promptBuilder.append("请使用Markdown格式来呈现你的故事，特别是在呈现选择时使用以下格式：");
            promptBuilder.append("\n\n### 现在，小朋友有几个选择：\n\n");
            promptBuilder.append("1. **选择一**：选择的详细描述\n");
            promptBuilder.append("2. **选择二**：选择的详细描述\n\n");
            promptBuilder.append("你想选择哪一个呢？\n\n");
            promptBuilder.append("请在比较复杂的汉字上方要标注拼音。请开始讲述故事开头，并提出第一个互动选择。");
        } else {
            // 故事继续的提示
            promptBuilder.append("继续之前的儿童互动故事。");
            promptBuilder.append("小朋友的输入是：").append(userInput).append("。");
            promptBuilder.append("请根据这个输入，继续发展故事情节，让故事更加生动有趣。");
            promptBuilder.append("记住故事是为").append(storyContext.getChildAge()).append("岁的小朋友设计的，");
            promptBuilder.append("应该简单易懂，富有教育意义。");
            promptBuilder.append("在合适的地方给出新的互动选择，不要太快结束故事。");
            promptBuilder.append("请使用Markdown格式来展示故事，特别是当呈现选择时，使用以下格式：");
            promptBuilder.append("\n\n### 现在，小朋友有几个选择：\n\n");
            promptBuilder.append("1. **选择一**：选择的详细描述\n");
            promptBuilder.append("2. **选择二**：选择的详细描述\n\n");
            promptBuilder.append("你想选择哪一个呢？\n\n");
        }

        return promptBuilder.toString();
    }

    private ChatRequest parseMessage(String payload) {
        try {
            // 使用Jackson或其他JSON库解析消息
            ObjectMapper objectMapper = new ObjectMapper();
            // 配置ObjectMapper忽略未知属性
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(payload, ChatRequest.class);
        } catch (Exception e) {
            log.error("解析故事消息失败: {}", payload, e);
            throw new RuntimeException("无效的消息格式: " + payload, e);
        }
    }

    /**
     * 根据用户ID和孩子信息获取或创建孩子记录
     */
    private Integer getChildIdFromUserId(String userId, String childName, int childAge) {
        if (userId == null || userId.isEmpty() || childName == null || childName.isEmpty()) {
            log.warn("用户ID或孩子名称为空，无法创建孩子记录");
            return null;
        }
        
        try {
            // 尝试将userId转换为整数
            Integer userIdInt = Integer.parseInt(userId);
            
            // 查找该用户下是否已有此名字的孩子
            List<Child> children = storyRecordService.findChildrenByUserId(userIdInt);
            
            // 查找匹配的孩子
            for (Child child : children) {
                if (childName.equals(child.getName())) {
                    log.info("找到匹配的孩子记录：ID={}, 名称={}", child.getId(), child.getName());
                    return child.getId();
                }
            }
            
            // 如果没有找到匹配的孩子，创建新的孩子记录
            Child newChild = new Child();
            newChild.setUserId(userIdInt);
            newChild.setName(childName);
            newChild.setAge(childAge);
            
            // 保存孩子记录
            Child savedChild = storyRecordService.createChild(newChild);
            if (savedChild != null) {
                log.info("创建新的孩子记录：ID={}, 名称={}", savedChild.getId(), savedChild.getName());
                return savedChild.getId();
            }
        } catch (NumberFormatException e) {
            log.error("用户ID格式不正确: {}", userId, e);
        } catch (Exception e) {
            log.error("获取或创建孩子记录时发生错误", e);
        }
        
        return null;
    }

    // 内部类：存储故事上下文
    static class StoryContext {
        private StringBuilder storyContent = new StringBuilder();
        private String storyType = "学习成长"; // 默认故事类型
        private String childName = "小朋友";
        private int childAge = 5;
        private Integer storyRecordId;

        public boolean isEmpty() {
            return storyContent.length() == 0;
        }

        public void addToStory(String content) {
            storyContent.append(content);
        }

        public String getStoryContent() {
            return storyContent.toString();
        }

        public void setStoryType(String storyType) {
            this.storyType = storyType;
        }

        public String getStoryType() {
            return storyType;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        public String getChildName() {
            return childName;
        }

        public void setChildAge(int childAge) {
            this.childAge = childAge;
        }

        public int getChildAge() {
            return childAge;
        }

        public Integer getStoryRecordId() {
            return storyRecordId;
        }

        public void setStoryRecordId(Integer storyRecordId) {
            this.storyRecordId = storyRecordId;
        }
    }
}

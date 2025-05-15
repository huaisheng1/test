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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final wsOpenAiChatModel openAiChatModel;
    private StoryRecordService  storyRecordService;
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);


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
                            // 评估阅读表现并调整难度
                            evaluateReadingPerformance(userInput, chunk, finalStoryContext);

                            // 添加日志输出，显示当前阅读能力
                            log.info("用户 [{}] 当前阅读能力: 级别={}, 词汇量={}, 句子复杂度={}",
                                    username,
                                    finalStoryContext.getReadingLevel(),
                                    finalStoryContext.getVocabularySize(),
                                    finalStoryContext.getSentenceComplexity());

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

//故事提示类，负责构建提示词
    private String buildStoryPrompt(String userInput, StoryContext storyContext) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一个专为3 - 6岁儿童设计的互动故事引导精灵。你的任务是：\n");
        promptBuilder.append("1. 创建一个充满想象力和互动性的故事体验\n");
        promptBuilder.append("2. 根据孩子的选择实时调整故事情节\n");
        promptBuilder.append("3. 让孩子真正参与到故事中，而不只是旁观者\n");
        promptBuilder.append("4. 通过故事自然地融入教育元素\n");
        promptBuilder.append("5. 使用丰富的表情符号和简单的拼音增强趣味性\n");
        promptBuilder.append("6. 创建多维度的选择分支，避免线性故事体验\n");

        promptBuilder.append("每次输出字数控制在50-60字之间。");
        promptBuilder.append("7. 在汉字上方标注拼音，使用格式：复杂的汉字（fù zá de hàn zì）\n");
        promptBuilder.append("8. 适当使用表情符号（如 📖✨🐾）来增强故事的趣味性\n");
        // 添加难度控制要求
        promptBuilder.append("9. 根据孩子的年龄和阅读能力动态调整故事难度\n");
        promptBuilder.append("10. 使用的词汇量应控制在").append(storyContext.getVocabularySize()).append("个常用词以内\n");
        promptBuilder.append("11. 句子复杂度应为").append(getSentenceComplexityDescription(storyContext.getSentenceComplexity())).append("\n");
        promptBuilder.append("12. 故事情节复杂度和挑战难度应与阅读能力等级").append(storyContext.getReadingLevel()).append("相匹配\n");

        // 为不同阅读能力级别添加具体指导
        promptBuilder.append(getReadingLevelGuidelines(storyContext.getReadingLevel()));



        if (storyContext.isEmpty()) {
            // 新故事开始的提示
            promptBuilder.append("你是一个专为6-9岁儿童设计的互动故事生成器。");
            promptBuilder.append("请为一个").append(storyContext.getChildAge()).append("岁的小朋友");
            promptBuilder.append("名叫").append(storyContext.getChildName()).append("创作一个关于。");
            promptBuilder.append(storyContext.getStoryTheme()).append("的互动故事。");

            if ("生活教育".equals(storyContext.getStoryType())) {
                promptBuilder.append("故事应该包含生活教育内容，如饮食卫生、作息规律、生活自理或安全自我保护。");
            } else if ("学习成长".equals(storyContext.getStoryType())) {
                promptBuilder.append("故事应该包含学习成长内容，如阅读、交流表达、益智教育、思维锻炼或艺术启发。");
            }
            promptBuilder.append("每次输出字数控制在50-60字之间。");
            promptBuilder.append("故事应该有生动有趣的情节，并在关键处给孩子选择，让他们参与到故事中。");
            promptBuilder.append("请使用Markdown格式来呈现你的故事，特别是在呈现选择时使用以下格式：");
            promptBuilder.append("\n\n### 现在，你有几个选择：\n\n");
            promptBuilder.append("1. **选择一**：选择的详细描述\n");
            promptBuilder.append("2. **选择二**：选择的详细描述\n\n");
            promptBuilder.append("你想选择哪一个呢？\n\n");
            promptBuilder.append("请在比较复杂的汉字上方要标注拼音，并适当使用表情符号。请开始讲述故事开头，并提出第一个互动选择。");
        } else {
            // 故事继续的提示
            promptBuilder.append("继续之前的儿童互动故事。");
            promptBuilder.append(storyContext.getStoryContent()).append("\n");
            promptBuilder.append("小朋友的输入是：").append(userInput).append("。");
            promptBuilder.append("请根据这个输入，继续发展故事情节，让故事更加生动有趣。");
            promptBuilder.append("记住故事是为").append(storyContext.getChildAge()).append("岁的小朋友设计的，");
            promptBuilder.append("应该简单易懂，富有教育意义。");
            promptBuilder.append("在合适的地方给出新的互动选择，不要太快结束故事。");
            promptBuilder.append("请使用Markdown格式来展示故事，特别是当呈现选择时，使用以下格式：");
            promptBuilder.append("\n\n### 现在，你有几个选择：\n\n");
            promptBuilder.append("1. **选择一**：选择的详细描述\n");
            promptBuilder.append("2. **选择二**：选择的详细描述\n\n");
            promptBuilder.append("你想选择哪一个呢？\n\n");
        }

        return promptBuilder.toString();
    }
    // 评估阅读表现并调整难度
    private void evaluateReadingPerformance(String userInput, String aiResponse, StoryContext context) {
        // 简单的难度评估逻辑，实际应用中可能需要更复杂的自然语言处理
        int interactionDifficulty = estimateDifficulty(aiResponse, context);

        // 评估回答正确性（简化版）
        boolean isCorrect = isAnswerCorrect(userInput, aiResponse);

        // 根据表现调整阅读能力
        context.adjustReadingLevel(isCorrect, interactionDifficulty);
    }

    // 估计AI回复的难度
    private int estimateDifficulty(String response, StoryContext context) {
        // 统计字数
        int charCount = response.length();

        // 统计复杂词汇（这里简化为超过4个汉字的词）
        int complexWordCount = 0;
        String[] words = response.split("\\s+");
        for (String word : words) {
            if (word.length() > 4) {
                complexWordCount++;
            }
        }

        // 统计句子复杂度（这里简化为逗号和句号数量）
        int punctuationCount = response.replaceAll("[^，。]", "").length();

        // 基于这些特征计算难度得分（简化算法）
        double difficultyScore =
                0.4 * Math.min(charCount / 20.0, 5.0) +
                        0.3 * Math.min(complexWordCount * 2, 5.0) +
                        0.3 * Math.min(punctuationCount / 3.0, 5.0);

        // 转换为难度等级（1-5）
        int difficultyLevel = Math.max(1, Math.min(5, (int) Math.round(difficultyScore)));

        log.info("估计互动难度: {}", difficultyLevel);
        return difficultyLevel;
    }

    // 评估用户回答是否正确（简化版）
    private boolean isAnswerCorrect(String userInput, String aiResponse) {
        // 简单的实现，实际应用中需要更复杂的逻辑
        // 这里假设只要用户输入不是空，就算回答正确
        return userInput != null && !userInput.trim().isEmpty();
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
    // 根据句子复杂度等级获取描述
    private String getSentenceComplexityDescription(int level) {
        switch (level) {
            case 1: return "简单（1-3个短句，无复杂结构）";
            case 2: return "中等（包含1-2个连接词，如'和'、'但是'）";
            case 3: return "稍复杂（包含复合句，如'因为...所以...'）";
            default: return "简单";
        }
    }
    // 根据阅读能力等级获取具体指导
    private String getReadingLevelGuidelines(int level) {
        StringBuilder guidelines = new StringBuilder();
        guidelines.append("\n\n针对阅读能力等级").append(level).append("的具体指导：\n");

        switch (level) {
            case 1:
                guidelines.append("- 使用最简单的词汇和句子结构\n");
                guidelines.append("- 每句话不超过10个汉字\n");
                guidelines.append("- 多使用重复和韵律\n");
                guidelines.append("- 重点使用常见物品、动作和情感词汇\n");
                guidelines.append("- 避免抽象概念和复杂情节\n");
                break;
            case 2:
                guidelines.append("- 使用基础词汇，避免生僻字\n");
                guidelines.append("- 每句话不超过15个汉字\n");
                guidelines.append("- 使用简单的形容词和动词\n");
                guidelines.append("- 故事情节清晰简单，有明确的开始和发展\n");
                break;
            case 3:
                guidelines.append("- 可以使用稍复杂的词汇，但需要标注拼音\n");
                guidelines.append("- 每句话不超过20个汉字\n");
                guidelines.append("- 可以包含简单的因果关系和对话\n");
                guidelines.append("- 故事可以有小转折，但保持线性结构\n");
                break;
            case 4:
                guidelines.append("- 使用丰富的词汇，适当引入新词汇并解释\n");
                guidelines.append("- 句子结构可以多样化\n");
                guidelines.append("- 可以包含更复杂的情感和角色互动\n");
                guidelines.append("- 故事可以有分支选择和小悬念\n");
                break;
            case 5:
                guidelines.append("- 使用较复杂的词汇和概念\n");
                guidelines.append("- 可以使用复合句和较长的段落\n");
                guidelines.append("- 包含更深入的角色发展和情节转折\n");
                guidelines.append("- 可以引入需要思考的问题和挑战\n");
                break;
        }

        return guidelines.toString();
    }

    // 内部类：存储故事上下文
    static class StoryContext {
        private StringBuilder storyContent = new StringBuilder();
        private String storyType = "学习成长"; // 默认故事类型
        private String childName = "小朋友";
        private int childAge = 6;
        private Integer storyRecordId;
        private String storyTheme;

        private int readingLevel = 1; // 1-5级，自动根据年龄计算，也可根据实际表现调整
        private int vocabularySize = 200; // 估计的词汇量，根据年龄和阅读表现动态调整
        private int sentenceComplexity = 1; // 句子复杂度等级，1-3级

        // 根据阅读表现调整阅读能力
        public void adjustReadingLevel(boolean correctAnswer, int difficulty) {
            // 如果回答正确且难度较高，提升阅读能力
            if (correctAnswer && difficulty >= readingLevel) {
                readingLevel = Math.min(5, readingLevel + 1);
                vocabularySize += 50;
                sentenceComplexity = Math.min(3, (readingLevel + 1) / 2);
                log.info("阅读能力提升！当前级别: {}", readingLevel);
            }
            // 如果回答错误且难度较低，降低阅读能力
            else if (!correctAnswer && difficulty < readingLevel) {
                readingLevel = Math.max(1, readingLevel - 1);
                vocabularySize = Math.max(150, vocabularySize - 50);
                sentenceComplexity = Math.max(1, (readingLevel + 1) / 2);
                log.info("阅读能力调整！当前级别: {}", readingLevel);
            }
        }

        // Getters and Setters
        public int getReadingLevel() { return readingLevel; }
        public int getVocabularySize() { return vocabularySize; }
        public int getSentenceComplexity() { return sentenceComplexity; }

        // 新增：根据年龄初始化阅读能力参数
        public StoryContext() {
            // 根据年龄自动设置初始阅读能力参数
            updateReadingLevelByAge();
        }
        // 根据年龄更新阅读能力参数
        public void updateReadingLevelByAge() {
            if (childAge < 4) {
                readingLevel = 1;
                vocabularySize = 150;
                sentenceComplexity = 1;
            } else if (childAge < 5) {
                readingLevel = 2;
                vocabularySize = 300;
                sentenceComplexity = 1;
            } else if (childAge < 6) {
                readingLevel = 3;
                vocabularySize = 450;
                sentenceComplexity = 2;
            } else if (childAge < 7) {
                readingLevel = 4;
                vocabularySize = 600;
                sentenceComplexity = 2;
            } else {
                readingLevel = 5;
                vocabularySize = 800;
                sentenceComplexity = 3;
            }
        }

        public String getStoryTheme() {
            return storyTheme;
        }

        public void setStoryTheme(String storyTheme) {
            this.storyTheme = storyTheme;
        }

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

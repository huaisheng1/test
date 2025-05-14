<template>
    <div class="background-view">
      <div class="storybook-view">
        <!-- 故事历史侧边栏 -->
        <div class="history-sidebar" :class="{ collapsed: sidebarCollapsed }">
          <div class="sidebar-header">
            <h3 v-show="!sidebarCollapsed">故事历史</h3>
            <div class="toggle-btn-container">
              <button class="toggle-btn" @click="toggleSidebar" :title="sidebarCollapsed ? '展开侧边栏' : '收起侧边栏'">
                <i :class="sidebarCollapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
              </button>
            </div>
          </div>
          
          <div v-if="historyLoading" class="sidebar-loading">
            <div class="loading-spinner"></div>
            <span v-show="!sidebarCollapsed">加载中...</span>
          </div>
          
          <div v-else-if="historyList.length === 0" class="empty-history">
            <i class="el-icon-document"></i>
            <p v-show="!sidebarCollapsed">还没有故事历史记录</p>
          </div>
          
          <div v-else class="history-list">
            <div 
              v-for="(item, index) in historyList" 
              :key="index" 
              class="history-item"
              @click="viewHistoryDetail(item.id)"
              :title="item.storyTheme"
            >
              <div class="history-item-header" v-show="!sidebarCollapsed">
                <span class="history-title">{{ item.storyTheme }}</span>
                <span class="history-type" :class="item.storyType">{{ item.storyType }}</span>
              </div>
              <div v-show="sidebarCollapsed" class="history-icon">
                <i class="el-icon-notebook-2"></i>
              </div>
              <div v-show="!sidebarCollapsed" class="history-item-date">{{ formatDate(item.createTime) }}</div>
              <div v-show="!sidebarCollapsed" class="history-item-duration">时长: {{ formatDuration(item.duration) }}</div>
            </div>
          </div>
          
          <div class="sidebar-footer">
            <el-button v-show="!sidebarCollapsed" size="small" type="info" @click="loadHistoryList">刷新</el-button>
            <i v-show="sidebarCollapsed" class="el-icon-refresh refresh-icon" @click="loadHistoryList" title="刷新历史记录"></i>
          </div>
          
          <!-- 悬浮收缩按钮 (在宽屏模式下显示) -->
          <div class="floating-toggle" v-if="!sidebarCollapsed && !isMobileView" @click="toggleSidebar" title="收起侧边栏">
            <i class="el-icon-d-arrow-left"></i>
          </div>
        </div>
        
        <div class="storybook-panel" :class="{ 'expanded': sidebarCollapsed }">
          <div class="message-panel">
            <div class="header">
              <div class="header-left">
                <button v-if="sidebarCollapsed" class="expand-sidebar-btn" @click="toggleSidebar" title="展开历史侧边栏">
                  <i class="el-icon-notebook-2"></i>
                </button>
                <h2>互动故事书</h2>
              </div>
              <div class="header-actions">
                <button class="mobile-sidebar-toggle" @click="toggleSidebar">
                  <i class="el-icon-notebook-2"></i> 历史
                </button>
                <el-button type="info" size="small" icon="el-icon-document" @click="goToHistory">历史记录</el-button>
                <span class="status-indicator" :class="{ online: isConnected }">
                  {{ isConnected ? '连接成功' : '连接中...' }}
                </span>
              </div>
            </div>
            
            <!-- 新故事选择区域 -->
            <div v-if="!storyStarted" class="story-setup">
              <div class="story-setup-container">
                <h3>开始一个新故事吧！</h3>
                
                <div class="setup-form">
                  <div class="form-item">
                    <label>小朋友的名字</label>
                    <div class="child-name-selector">
                      <el-select 
                        v-model="selectedChild" 
                        filterable 
                        placeholder="选择或创建小朋友" 
                        @change="handleChildSelect"
                        style="width: 100%;"
                      >
                        <el-option-group label="已有小朋友">
                          <el-option 
                            v-for="child in childrenList" 
                            :key="child.id" 
                            :label="child.name + ' (' + child.age + '岁)'" 
                            :value="child.id">
                          </el-option>
                        </el-option-group>
                        <el-option value="create-new" label="+ 创建新小朋友"></el-option>
                      </el-select>
                      
                      <div v-if="showCreateChild" class="create-child-form">
                        <el-input v-model="childName" placeholder="请输入小朋友的名字"></el-input>
                        <el-select v-model="childAge" placeholder="选择年龄">
                          <el-option v-for="age in [6,7,8,9]" :key="age" :label="`${age}岁`" :value="age"></el-option>
                        </el-select>
                        <el-select v-model="childGender" placeholder="选择性别">
                          <el-option label="男孩" value="男"></el-option>
                          <el-option label="女孩" value="女"></el-option>
                        </el-select>
                        <div class="child-form-actions">
                          <el-button type="primary" size="small" @click="createNewChild" :loading="creatingChild">保存</el-button>
                          <el-button size="small" @click="cancelCreateChild">取消</el-button>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div v-if="!showCreateChild" class="form-item">
                    <label>小朋友的年龄</label>
                    <el-select v-model="childAge" placeholder="选择年龄" :disabled="selectedChild && selectedChild !== 'create-new'">
                      <el-option v-for="age in [6,7,8,9]" :key="age" :label="`${age}岁`" :value="age"></el-option>
                    </el-select>
                  </div>
                  
                  <div class="form-item">
                    <label>故事类型</label>
                    <el-radio-group v-model="storyType">
                      <el-radio label="生活教育">生活教育</el-radio>
                      <el-radio label="学习成长">学习成长</el-radio>
                    </el-radio-group>
                  </div>
                </div>
                
                <div class="story-themes">
                  <h4>故事主题</h4>
                  <div class="theme-cards">
                    <div 
                      v-for="(theme, index) in storyThemes[storyType]" 
                      :key="index" 
                      class="theme-card"
                      @click="selectTheme(theme)"
                      :class="{ selected: selectedTheme === theme }"
                    >
                      <div class="theme-icon">🔮</div>
                      <div class="theme-name">{{ theme }}</div>
                    </div>
                  </div>
                </div>
                
                <el-button type="primary" @click="startStory" :disabled="!canStartStory">
                  开始故事
                </el-button>
              </div>
            </div>
            
            <!-- 故事内容区域 -->
            <div v-else class="story-content">
              <!-- 故事消息列表 -->
               <div class="message-list" ref="messageContainer">
                <div
                  v-for="(message, index) in messages"
                  :key="index"
                  :class="['message-row', message.type]"
                >
                  <div v-if="message.type === 'ai'" class="message-avatar">
                    <img :src="aiAvatar" alt="故事精灵" class="avatar" @error="handleAvatarError($event, 'ai')" />
                  </div>
                  <div :class="['message', message.type === 'user' ? 'user-message' : 'ai-message']">
                    <div v-html="formatContentWithPinyin(message.content)"></div>
                  </div>
                   <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
                  <!-- 添加播放语音按钮 -->
                    <el-button
                    v-if="message.type === 'ai'"
                    type="text"
                    @click="toggleSpeech(message)"
                  >
                    <i class="el-icon-volume-up"></i> {{ getButtonText(message) }}
                  </el-button>
                
                  <div v-if="message.type === 'user'" class="message-avatar">
                    <img :src="userAvatar || generateUserAvatar" alt="小朋友头像" class="avatar" @error="handleAvatarError($event, 'user')" />
                  </div>
                </div>
              </div>
  
  
              <!-- 互动输入区域 -->
              <div class="message-input">
                <el-input
                  v-model="userInput"
                  type="textarea"
                  :rows="2"
                  :placeholder="inputPlaceholder"
                  @keyup.enter.native="sendMessage"
                />
                <div class="control-buttons">
                  <el-button type="warning" @click="restartStory">
                    重新开始
                  </el-button>
                  <el-button type="primary" @click="sendMessage" :loading="loading">
                    <i class="el-icon-s-promotion"></i> 发送
                  </el-button>
                  <el-button type="success" @click="toggleVoiceInput">
                    <i :class="isListening ? 'el-icon-microphone' : 'el-icon-turn-off-microphone'"></i> 
                    {{ isListening ? '正在听...' : '语音输入' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import userAvatarImg from '@/assets/user.jpg';
  import aiAvatarImg from '@/assets/ai.jpg';
  import axios from '../utils/axios';

  // 路由
  const router = useRouter();

  // 用于控制滚动更新的计时器
  let scrollUpdateTimeout = null;

  // 侧边栏状态
  const sidebarCollapsed = ref(localStorage.getItem('sidebarCollapsed') === 'true');
  const historyList = ref([]);
  const historyLoading = ref(false);
  const isMobileView = ref(window.innerWidth <= 768);

  // 消息数据
  const messages = ref([]);
  const userInput = ref('');
  const loading = ref(false);
  const socket = ref(null);
  const isConnected = ref(false);
  const reconnectAttempts = ref(0);
  const maxReconnectAttempts = 5;
  const reconnectInterval = 3000;

  // 故事设置
  const storyStarted = ref(false);
  const childName = ref('');
  const childAge = ref(5);
  const childGender = ref('');
  const storyType = ref('学习成长');
  const childrenList = ref([]);
  const selectedChild = ref(null);
  const showCreateChild = ref(false);
  const creatingChild = ref(false);
  const storyThemes = reactive({
    '生活教育': [
      '刷牙洗脸', '健康饮食', '睡前故事', 
      '安全知识', '公共礼仪', '整理玩具'
    ],
    '学习成长': [
      '友谊分享', '情绪管理', '动物世界', 
      '探索宇宙', '字母数字', '音乐艺术'
    ]
  });
  const selectedTheme = ref('');
  const startTime = ref(0);
  const childId = ref(null);

  // 头像
  const userAvatar = ref(userAvatarImg);
  const aiAvatar = ref(aiAvatarImg);

  // 语音识别
  const recognition = ref(null);
  const isListening = ref(false);

  // 从 localStorage 获取用户信息
  const userId = ref(localStorage.getItem('userId') || 'guest-user');

  // DOM引用
  const messageContainer = ref(null);

  // 计算属性
  const inputPlaceholder = computed(() => {
    return isListening.value ? '正在聆听你的声音...' : '小朋友，你想怎么做呢？';
  });

  const canStartStory = computed(() => {
    if (showCreateChild.value) {
      return childName.value.trim() && childAge.value && storyType.value && selectedTheme.value;
    }
    return ((selectedChild.value && selectedChild.value !== 'create-new') || childName.value.trim()) && 
      childAge.value && storyType.value && selectedTheme.value;
  });

  const generateUserAvatar = computed(() => {
    return `https://api.dicebear.com/7.x/adventurer/svg?seed=${childName.value || 'child'}`;
  });
  // 语音合成实例
const synth = window.speechSynthesis;
// 用于存储每个消息的语音播放状态
const speechStates = ref({});
// 切换语音播放状态
const toggleSpeech = (message) => {
  const { id } = message;
  const currentState = speechStates.value[id];

  if (!currentState) {
    // 第一次点击，开始播放
    playSpeech(message.content);
    speechStates.value[id] = { playing: true, paused: false };
  } else if (currentState.playing &&!currentState.paused) {
    // 正在播放，点击暂停
    synth.pause();
    speechStates.value[id].paused = true;
  } else if (currentState.playing && currentState.paused) {
    // 已暂停，点击继续播放
    synth.resume();
    speechStates.value[id].paused = false;
  }
};
// 播放语音方法
const playSpeech = (text) => {
  if (synth.speaking) {
    synth.cancel();
  }
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = 'zh-CN';
  synth.speak(utterance);
};
// 获取按钮文字
const getButtonText = (message) => {
  const { id } = message;
  const currentState = speechStates.value[id];

  if (!currentState) {
    return '播放语音';
  } else if (currentState.playing &&!currentState.paused) {
    return '暂停';
  } else if (currentState.playing && currentState.paused) {
    return '继续播放';
  }
};
// 格式化消息时间
const formatMessageTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};
  // 获取本地存储的设置
const storedSettings = JSON.parse(localStorage.getItem('settings')) || {};

onMounted(() => {
  // 应用主题模式
  if (storedSettings.themeMode === 'dark') {
    document.body.classList.add('dark-mode');
  } else {
    document.body.classList.remove('dark-mode');
  }

  // 应用侧边栏固定设置
  // 这里需要根据实际的侧边栏组件逻辑进行调整
  sidebarCollapsed.value = !storedSettings.sidebarFixed;

  // 应用消息气泡形状设置
  // 这里需要根据实际的消息气泡样式进行调整
  if (storedSettings.messageBubbleShape === 'square') {
    // 添加或修改消息气泡样式为直角
    
  } else {
    // 添加或修改消息气泡样式为圆角
  }

  // 应用字体大小设置
  document.documentElement.style.fontSize = storedSettings.fontSize === 'small' ? '14px' : storedSettings.fontSize === 'medium' ? '18px' : '22px';

});
  // 生命周期钩子
  onMounted(() => {
    // 初始化语音识别
    initSpeechRecognition();

    // 从localStorage获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        userId.value = user.id;
        
        // 加载历史记录列表
        loadHistoryList();
        
        // 加载该用户的小朋友列表
        loadChildrenList();
      } catch (error) {
        console.error('解析用户信息失败', error);
      }
    }
    
    // 添加窗口大小变化监听
    window.addEventListener('resize', handleResize);
    
    // 动态加载markdown解析库
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/marked/marked.min.js';
    script.onload = () => {
      console.log('Markdown parser loaded');
    };
    script.onerror = (err) => {
      console.error('Failed to load markdown parser:', err);
    };
    document.head.appendChild(script);
  });

  onBeforeUnmount(() => {
    closeWebSocket();
    // 停止语音识别
    if (recognition.value) {
      recognition.value.stop();
    }
    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize);
  });

  // 方法
  function initSpeechRecognition() {
    if ('webkitSpeechRecognition' in window) {
      recognition.value = new webkitSpeechRecognition();
      recognition.value.continuous = false;
      recognition.value.interimResults = false;
      recognition.value.lang = 'zh-CN';
      
      recognition.value.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        userInput.value = transcript;
        isListening.value = false;
        // 自动发送识别到的内容
        sendMessage();
      };
      
      recognition.value.onerror = (event) => {
        console.error('语音识别错误:', event.error);
        isListening.value = false;
        ElMessage.error('语音识别失败，请重试或使用文字输入');
      };
      
      recognition.value.onend = () => {
        isListening.value = false;
      };
    } else {
      ElMessage.warning('你的浏览器不支持语音识别功能');
    }
  }

  function toggleVoiceInput() {
    if (!recognition.value) {
      ElMessage.warning('你的浏览器不支持语音识别功能');
      return;
    }
    
    if (isListening.value) {
      recognition.value.stop();
      isListening.value = false;
    } else {
      recognition.value.start();
      isListening.value = true;
      ElMessage.success('开始聆听，请对着麦克风说话...');
    }
  }

  function selectTheme(theme) {
    selectedTheme.value = theme;
  }

  function startStory() {
    if (!canStartStory.value) {
      ElMessage.warning('请完善故事设置');
      return;
    }
    
    storyStarted.value = true;
    startTime.value = new Date().getTime();
    
    // 添加初始AI消息
    messages.value = [{
      type: 'ai',
      content: '故事精灵正在准备一个精彩的故事...'
    }];
    
    // 建立WebSocket连接
    setupWebSocket();
    
    // 短暂延迟后发送故事开始消息
    setTimeout(() => {
      sendStoryStartMessage();
    }, 1000);
  }

  function restartStory() {
    ElMessageBox.confirm('确定要重新开始故事吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 如果故事已经开始过，则保存历史记录
      if (storyStarted.value && messages.value.length > 1) {
        saveStoryHistory();
      }
      
      storyStarted.value = false;
      messages.value = [];
      closeWebSocket();
    }).catch(() => {});
  }

  function sendStoryStartMessage() {
    // 创建一个初始故事提示
    const storyPrompt = `我想听一个关于"${selectedTheme.value}"的故事`;
    
    // 准备发送内容
    const message = {
      type: 'story',
      text: storyPrompt,
      username: childName.value,
      userId: userId.value,
      childId: childId.value,
      isNewStory: true,
      childAge: childAge.value,
      storyType: storyType.value,
      storyTheme: selectedTheme.value,
      startTime: new Date().getTime()
    };
    
    loading.value = true;
    
    // 发送消息
    if (socket.value.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(message));
      console.log('故事开始请求已发送:', message);
    } else {
      ElMessage.error('连接未就绪，请稍后再试');
      loading.value = false;
      // 尝试重连
      handleReconnect();
    }
  }

  function setupWebSocket() {
    try {
      // 关闭已有连接
      if (socket.value) {
        socket.value.close();
        socket.value = null;
      }

      // 构建WebSocket URL
      const wsUrl = `ws://localhost:8080/ws/story`;
      
      console.log('正在连接到故事WebSocket服务器:', wsUrl);
      socket.value = new WebSocket(wsUrl);
      
      // 连接超时处理
      const connectionTimeout = setTimeout(() => {
        if (!isConnected.value) {
          console.error('WebSocket连接超时');
          closeWebSocket();
          handleReconnect();
        }
      }, 5000);

      // 连接成功
      socket.value.onopen = (event) => {
        clearTimeout(connectionTimeout);
        console.log('故事WebSocket连接已建立');
        isConnected.value = true;
        reconnectAttempts.value = 0;
        ElMessage.success('已连接到故事精灵');
      };

      // 接收消息
      socket.value.onmessage = (event) => {
        try {
          let message = event.data;
          if (message.startsWith('data:')) {
            message = message.substring(5);
            // 检查结束标记
            if (message.includes('[DONE]')) {
              loading.value = false;
              console.log('故事响应完成');
              scrollToBottom(); // 确保最终滚动到底部
              return;
            }
            
            // 规范化处理消息格式 - 移除不必要的换行和确保适当的空格
            message = message.trim();
            
            // 更新或创建AI消息
            if (messages.value.length > 0 && messages.value[messages.value.length - 1].type === 'ai') {
              // 获取最后一条消息
              const lastMessage = messages.value[messages.value.length - 1];
              
              // 添加一个空格，如果上一个字符不是空格或换行，且当前消息不以标点符号开始
              if (lastMessage.content.length > 0 && 
                  !/[\s\n]$/.test(lastMessage.content) && 
                  !/^[，。！？,.!?]/.test(message)) {
                message = ' ' + message;
              }
              
              // 更新最后一条AI消息
              lastMessage.content += message;
            } else {
              // 创建新的AI消息
              messages.value.push({
                type: 'ai',
                content: message
              });
            }
            
            // 定期滚动到底部，避免频繁滚动
            if (scrollUpdateTimeout) {
              clearTimeout(scrollUpdateTimeout);
            }
            scrollUpdateTimeout = setTimeout(() => {
              scrollToBottom();
              scrollUpdateTimeout = null;
            }, 300);
          }
        } catch (error) {
          console.error('处理WebSocket消息时出错:', error);
          ElMessage.error('消息处理出错');
        }
      };

      // 错误处理
      socket.value.onerror = (error) => {
        console.error('WebSocket错误:', error);
        isConnected.value = false;
        handleReconnect();
      };

      // 连接关闭
      socket.value.onclose = (event) => {
        console.log('WebSocket连接已关闭', event);
        isConnected.value = false;
        if (!event.wasClean) {
          handleReconnect();
        }
      };
    } catch (error) {
      console.error('WebSocket创建失败:', error);
      ElMessage.error(`连接失败: ${error.message}`);
      handleReconnect();
    }
  }

  function handleReconnect() {
    if (reconnectAttempts.value >= maxReconnectAttempts) {
      ElMessage.error('无法连接到故事服务，请稍后再试');
      isConnected.value = false;
      return;
    }

    reconnectAttempts.value++;
    const nextAttemptIn = reconnectInterval / 1000;
    ElMessage.warning(`连接失败，${nextAttemptIn}秒后尝试第${reconnectAttempts.value}次重连...`);

    setTimeout(() => {
      setupWebSocket();
    }, reconnectInterval);
  }

  function closeWebSocket() {
    if (socket.value) {
      socket.value.close();
      socket.value = null;
      isConnected.value = false;
    }
  }

  async function sendMessage() {
    if (!userInput.value.trim()) return;

    // 检查连接状态
    if (!isConnected.value) {
      ElMessage.warning('正在连接到故事精灵...');
      setupWebSocket();
      return;
    }

    // 添加用户消息
    messages.value.push({
      type: 'user',
      content: userInput.value
    });
    
    // 准备发送内容
    const userMessage = userInput.value;
    userInput.value = '';

    loading.value = true;
    scrollToBottom();

    try {
      // 构建消息对象
      const message = {
        type: 'story',
        text: userMessage,
        username: childName.value,
        userId: userId.value,
        childId: childId.value,
        isNewStory: false,
        childAge: childAge.value,
        storyType: storyType.value
      };
      
      // 发送消息
      if (socket.value.readyState === WebSocket.OPEN) {
        socket.value.send(JSON.stringify(message));
        console.log('故事交互已发送:', message);
      } else {
        throw new Error('WebSocket连接未就绪');
      }
    } catch (error) {
      console.error('发送消息失败:', error);
      ElMessage.error('发送失败，请重试');
      loading.value = false;
      // 尝试重连
      handleReconnect();
    }
  }

  function formatContentWithPinyin(content) {
    // 如果标记库已加载和内容存在
    if (window.marked && content) {
      try {
        // 处理文本格式，规范化换行和空格
        let formattedContent = content
          // 替换掉多余的换行符，规范化空白符
          .replace(/\n\s*\n\s*\n/g, '\n\n')
          // 确保在汉字之间的断行点有适当的空格
          .replace(/([^\s])\n([^\s])/g, '$1 $2');
        
        // 处理拼音标注格式，将形如 "字（zi）" 的格式转换为 ruby 标签
        formattedContent = formattedContent.replace(/([一-龥])（([a-zA-Z]+)）/g, '<ruby>$1<rt>$2</rt></ruby>');
        
        // 解析Markdown内容
        let parsedContent = window.marked.parse(formattedContent);
        
        // 修复可能的排版问题
        parsedContent = parsedContent
          // 消除段落之间可能的不必要间隔
          .replace(/<\/p>\s*<p>/g, '</p><p>')
          // 确保汉字之间的空格不会导致不必要的换行
          .replace(/([一-龥])\s+([一-龥])/g, '$1$2');
        
        return parsedContent;
      } catch (error) {
        console.error('解析Markdown时出错:', error);
        return content;
      }
    }
    // 如果标记库未加载，返回原始内容
    return content;
  }

  function scrollToBottom() {
    nextTick(() => {
      const container = messageContainer.value;
      if (container) {
        // Use a slight delay to ensure content has rendered
        setTimeout(() => {
          container.scrollTop = container.scrollHeight;
        }, 50);
      }
    });
  }

  function handleAvatarError(event, type) {
    // 头像加载失败时的处理
    if (type === 'user') {
      event.target.src = 'https://api.dicebear.com/7.x/adventurer/svg?seed=child';
    } else {
      event.target.src = 'https://api.dicebear.com/7.x/bottts/svg?seed=storybot';
    }
  }

  // 添加保存故事历史记录方法
  async function saveStoryHistory() {
    if (!userId.value) {
      console.warn('用户未登录，无法保存历史记录');
      return;
    }
    
    try {
      // 过滤出用户和AI的对话内容
      const dialogMessages = messages.value.filter(msg => msg.type === 'user' || msg.type === 'ai');
      
      // 计算故事持续时间（秒）
      const duration = Math.round((new Date().getTime() - startTime.value) / 1000);
      
      // 准备发送内容
      const historyData = {
        userId: userId.value,
        childId: childId.value,
        childName: childName.value,
        childAge: childAge.value,
        storyType: storyType.value,
        storyTheme: selectedTheme.value,
        duration: duration,
        messages: dialogMessages.map((msg, index) => ({
          content: msg.content,
          speaker: msg.type,
          sequence: index + 1
        }))
      };
      
      // 发送请求保存历史记录
      const { data } = await axios.post('/api/story/save-history', historyData);
      
      if (data.code === 200) {
        console.log('故事历史记录保存成功');
      } else {
        console.error('保存历史记录失败:', data.message);
      }
    } catch (error) {
      console.error('保存历史记录异常:', error);
    }
  }

  function goToHistory() {
    // 保存当前故事再跳转
    if (storyStarted.value && messages.value.length > 1) {
      saveStoryHistory();
    }
    router.push('/history');
  }

  // 切换侧边栏显示状态
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value;
    localStorage.setItem('sidebarCollapsed', sidebarCollapsed.value);
  }

  // 加载历史记录列表
  async function loadHistoryList() {
    try {
      historyLoading.value = true;
      
      // 从localStorage获取用户ID
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        ElMessage.warning('请先登录');
        historyLoading.value = false;
        return;
      }
      
      const user = JSON.parse(userStr);
      const { data } = await axios.get(`/api/story-record/user/${user.id}`);
      
      if (data.code === 200 && data.data) {
        historyList.value = data.data.sort((a, b) => {
          // 按创建时间倒序排列
          return new Date(b.createTime) - new Date(a.createTime);
        });
      } else {
        historyList.value = [];
      }
    } catch (error) {
      console.error('加载历史记录失败:', error);
      ElMessage.error('获取历史记录失败');
      historyList.value = [];
    } finally {
      historyLoading.value = false;
    }
  }

  // 查看历史记录详情
  function viewHistoryDetail(recordId) {
    router.push(`/history/${recordId}`);
  }

  // 格式化日期
  function formatDate(dateStr) {
    if (!dateStr) return '未知时间';
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }

  // 格式化时长
  function formatDuration(seconds) {
    if (!seconds || seconds <= 0) return '0分钟';
    if (seconds < 60) return `${seconds}秒`;
    const minutes = Math.floor(seconds / 60);
    const remainSeconds = seconds % 60;
    return `${minutes}分${remainSeconds > 0 ? remainSeconds + '秒' : ''}`;
  }

  // 监听窗口大小变化
  function handleResize() {
    isMobileView.value = window.innerWidth <= 768;
  }

  // 加载该用户的小朋友列表
  async function loadChildrenList() {
    try {
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        return;
      }
      
      const user = JSON.parse(userStr);
      const { data } = await axios.get(`/api/child/user/${user.id}`);
      
      if (data.code === 200) {
        childrenList.value = data.data || [];
        // 如果有小朋友，默认选择第一个
        if (childrenList.value.length > 0) {
          selectedChild.value = childrenList.value[0].id;
          childName.value = childrenList.value[0].name;
          childAge.value = childrenList.value[0].age;
          childGender.value = childrenList.value[0].gender || '';
          childId.value = childrenList.value[0].id;
        }
      }
    } catch (error) {
      console.error('获取小朋友列表失败:', error);
      ElMessage.error('获取小朋友列表失败');
    }
  }

  // 处理小朋友选择
  function handleChildSelect(value) {
    if (value === 'create-new') {
      // 选择创建新小朋友
      showCreateChild.value = true;
      childName.value = '';
      childAge.value = 5;
      childGender.value = '';
      childId.value = null;
    } else {
      // 选择已有小朋友
      showCreateChild.value = false;
      const selectedChildData = childrenList.value.find(child => child.id === value);
      if (selectedChildData) {
        childName.value = selectedChildData.name;
        childAge.value = selectedChildData.age;
        childGender.value = selectedChildData.gender || '';
        childId.value = selectedChildData.id;
      }
    }
  }

  // 创建新小朋友
  async function createNewChild() {
    if (!childName.value.trim()) {
      ElMessage.warning('请输入小朋友的名字');
      return;
    }
    
    try {
      creatingChild.value = true;
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        ElMessage.warning('请先登录');
        creatingChild.value = false;
        return;
      }
      
      const user = JSON.parse(userStr);
      const childData = {
        userId: user.id,
        name: childName.value.trim(),
        age: childAge.value,
        gender: childGender.value
      };
      
      const { data } = await axios.post('/api/child/add', childData);
      
      if (data.code === 200) {
        ElMessage.success('创建小朋友成功');
        // 添加到列表
        childrenList.value.push(data.data);
        // 选择新创建的小朋友
        selectedChild.value = data.data.id;
        childId.value = data.data.id;
        showCreateChild.value = false;
      } else {
        ElMessage.error(data.message || '创建小朋友失败');
      }
    } catch (error) {
      console.error('创建小朋友失败:', error);
      ElMessage.error('创建小朋友失败');
    } finally {
      creatingChild.value = false;
    }
  }

  // 取消创建新小朋友
  function cancelCreateChild() {
    showCreateChild.value = false;
    // 如果有小朋友，重新选择第一个
    if (childrenList.value.length > 0) {
      selectedChild.value = childrenList.value[0].id;
      handleChildSelect(selectedChild.value);
    } else {
      selectedChild.value = null;
      childName.value = '';
      childId.value = null;
    }
  }
  </script>
  
  <style scoped>
  .background-view {
    min-height: 100vh;
    background-color: #f0f8ff;
    background-image: linear-gradient(135deg, #c9f0ff 0%, #f5f7fa 100%);
  }
  /* 深色模式样式 */
.dark-mode {
  background-color: #121212;
  color: white;
}
  .storybook-view {
    display: flex;
    justify-content: flex-start;
    align-items: stretch;
    min-height: 100vh;
    padding: 20px;
    height: 100%;
  }
  
  /* 历史记录侧边栏样式 */
  .history-sidebar {
    width: 280px;
    height: calc(100vh - 40px);
    background-color: white;
    border-radius: 16px 0 0 16px;
    box-shadow: 0 4px 20px rgba(0, 123, 255, 0.15);
    display: flex;
    flex-direction: column;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;
    z-index: 10;
    margin-right: 15px;
    position: relative;
  }
  
  .history-sidebar.collapsed {
    width: 50px;
    min-width: 50px;
    margin-right: 10px;
    box-shadow: 0 4px 15px rgba(0, 123, 255, 0.1);
  }
  
  .sidebar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    background: linear-gradient(90deg, #5e72e4 0%, #825ee4 100%);
    color: white;
  }
  
  .sidebar-header h3 {
    margin: 0;
    font-size: 18px;
    white-space: nowrap;
    transition: opacity 0.2s;
  }
  
  .toggle-btn-container {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .toggle-btn {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    font-size: 16px;
    z-index: 5;
    padding: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    height: 28px;
    width: 28px;
    transition: all 0.2s;
    background-color: rgba(255, 255, 255, 0.1);
  }
  
  .toggle-btn:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: scale(1.1);
  }
  
  .toggle-btn:active {
    transform: scale(0.95);
  }
  
  .floating-toggle {
    position: absolute;
    right: -15px;
    top: 50%;
    transform: translateY(-50%);
    width: 30px;
    height: 50px;
    background-color: white;
    border-radius: 0 8px 8px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    box-shadow: 4px 0 10px rgba(0, 0, 0, 0.1);
    z-index: 5;
    opacity: 0;
    transition: opacity 0.3s;
  }
  
  .history-sidebar:hover .floating-toggle {
    opacity: 1;
  }
  
  .floating-toggle i {
    color: #5e72e4;
    font-size: 14px;
  }
  
  .history-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    scrollbar-width: thin;
    scrollbar-color: #d0d0d0 #f5f5f5;
  }
  
  .history-list::-webkit-scrollbar {
    width: 6px;
  }
  
  .history-list::-webkit-scrollbar-track {
    background: #f5f5f5;
  }
  
  .history-list::-webkit-scrollbar-thumb {
    background-color: #d0d0d0;
    border-radius: 6px;
  }
  
  .history-item {
    background-color: #f8f9fe;
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 10px;
    cursor: pointer;
    transition: all 0.2s;
    border-left: 4px solid #5e72e4;
    position: relative;
    overflow: hidden;
  }
  
  .history-item:hover {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
    background-color: #f0f4ff;
  }
  
  .history-item:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .history-icon {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 20px;
    color: #5e72e4;
    height: 24px;
  }
  
  .history-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
  }
  
  .history-title {
    font-weight: bold;
    font-size: 14px;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 150px;
  }
  
  .history-type {
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 10px;
    color: white;
  }
  
  .history-type.学习成长 {
    background-color: #2dce89;
  }
  
  .history-type.生活教育 {
    background-color: #fb6340;
  }
  
  .history-item-date {
    font-size: 12px;
    color: #777;
    margin-bottom: 4px;
  }
  
  .history-item-duration {
    font-size: 12px;
    color: #555;
  }
  
  .sidebar-footer {
    padding: 15px;
    display: flex;
    justify-content: center;
    border-top: 1px solid #eaeaea;
  }
  
  .refresh-icon {
    font-size: 20px;
    cursor: pointer;
    color: #5e72e4;
    transition: transform 0.5s ease;
  }
  
  .refresh-icon:hover {
    transform: rotate(180deg);
  }
  
  .sidebar-loading, .empty-history {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: #888;
  }
  
  .loading-spinner {
    width: 30px;
    height: 30px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #5e72e4;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 10px;
  }
  
  .collapsed .history-item {
    padding: 8px 5px;
    text-align: center;
    border-left-width: 2px;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .empty-history i {
    font-size: 32px;
    margin-bottom: 10px;
  }
  
  .empty-history p {
    margin: 0;
  }
  
  .mobile-sidebar-toggle {
    display: none;
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    margin-right: 10px;
  }
  
  .storybook-panel {
    flex: 1;
    background-color: white;
    border-radius: 16px;
    box-shadow: 0 6px 30px rgba(0, 123, 255, 0.15);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: calc(100vh - 40px);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .storybook-panel.expanded {
    max-width: calc(100% - 60px);
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 25px;
    background: linear-gradient(90deg, #6a11cb 0%, #2575fc 100%);
    color: white;
  }
  
  .header-left {
    display: flex;
    align-items: center;
  }
  
  .header h2 {
    margin: 0;
    font-size: 24px;
    font-weight: bold;
  }
  
  .expand-sidebar-btn {
    background: none;
    border: none;
    color: white;
    margin-right: 15px;
    cursor: pointer;
    font-size: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 5px;
    border-radius: 50%;
    transition: background-color 0.2s;
  }
  
  .expand-sidebar-btn:hover {
    background-color: rgba(255, 255, 255, 0.2);
  }
  
  @media (max-width: 768px) {
    .history-sidebar {
      position: fixed;
      left: 0;
      top: 0;
      height: 100vh;
      transform: translateX(0);
      z-index: 1000;
      border-radius: 0;
    }
    
    .history-sidebar.collapsed {
      transform: translateX(-100%);
      width: 280px; /* 在移动端保持宽度不变，但完全隐藏 */
    }
    
    .mobile-sidebar-toggle {
      display: block;
    }

    .storybook-panel {
      width: 100% !important;
      border-radius: 10px;
    }
    
    .floating-toggle {
      display: none;
    }
  }
  
  .message-panel {
    display: flex;
    flex-direction: column;
    height: 100%;
    max-height: calc(100vh - 40px);
    overflow: hidden;
  }

  .header-actions {
    display: flex;
    align-items: center;
  }

  .status-indicator {
    font-size: 14px;
    padding: 4px 12px;
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
  }

  .status-indicator.online {
    background-color: rgba(103, 194, 58, 0.8);
  }

  /* 故事设置区域 */
  .story-setup {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    overflow-y: auto;
  }

  .story-setup-container {
    max-width: 600px;
    width: 100%;
    text-align: center;
  }

  .story-setup h3 {
    font-size: 28px;
    color: #6a11cb;
    margin-bottom: 30px;
  }

  .setup-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-bottom: 30px;
  }

  .form-item {
    display: flex;
    flex-direction: column;
    text-align: left;
  }

  .form-item label {
    font-size: 16px;
    margin-bottom: 8px;
    color: #333;
  }

  .story-themes {
    margin-bottom: 30px;
  }

  .story-themes h4 {
    font-size: 18px;
    color: #333;
    margin-bottom: 15px;
  }

  .theme-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
    justify-content: center;
  }

  .theme-card {
    background-color: #f5f7fa;
    border-radius: 12px;
    padding: 15px 10px;
    cursor: pointer;
    transition: all 0.3s;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }

  .theme-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  }

  .theme-card.selected {
    background-color: #e0f0ff;
    border: 2px solid #2575fc;
  }

  .theme-icon {
    font-size: 28px;
    margin-bottom: 10px;
  }

  .theme-name {
    font-size: 14px;
    color: #333;
  }

  /* 故事内容区域 */
  .story-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    max-height: calc(100vh - 200px);
    height: 100%;
    overflow: hidden;
  }

  .message-list {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background-color: #f9fafc;
    scroll-behavior: smooth;
    display: flex;
    flex-direction: column;
    max-height: calc(100vh - 300px);
  }

  .message-row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20px;
    width: 100%;
  }

  .message-row.user {
    justify-content: flex-end;
  }

  .message-row.ai {
    justify-content: flex-start;
  }

  .message-avatar {
    margin: 0 10px;
    flex-shrink: 0;
  }

  .avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  }

  .message {
    max-width: 70%;
    padding: 15px 20px;
    border-radius: 16px;
    font-size: 15px;
    line-height: 1.6;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    word-wrap: break-word;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .user-message {
    background-color: #6a11cb;
    background-image: linear-gradient(to right, #6a11cb, #2575fc);
    color: white;
    border-bottom-right-radius: 4px;
  }

  .ai-message {
    background-color: white;
    color: #333;
    border: 1px solid #eaeaea;
    border-bottom-left-radius: 4px;
  }

  .message-input {
    padding: 15px 20px;
    border-top: 1px solid #eef2f7;
    background-color: white;
    flex-shrink: 0;
  }

  .message-input .el-textarea {
    margin-bottom: 10px;
  }

  .control-buttons {
    display: flex;
    justify-content: space-between;
  }

  /* Deep selectors for markdown content */
  :deep(.message .markdown) {
    white-space: pre-wrap;
  }

  :deep(.message h1), :deep(.message h2), :deep(.message h3) {
    margin: 10px 0;
    font-weight: bold;
  }

  :deep(.message h1) {
    font-size: 1.3em;
  }

  :deep(.message h2) {
    font-size: 1.2em;
  }

  :deep(.message h3) {
    font-size: 1.1em;
  }

  :deep(.message ul), :deep(.message ol) {
    padding-left: 20px;
    margin: 10px 0;
  }

  :deep(.message li) {
    margin: 5px 0;
  }

  :deep(.message p) {
    margin: 8px 0;
  }

  :deep(.message code) {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 2px 4px;
    border-radius: 3px;
  }

  :deep(.message pre) {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 10px;
    border-radius: 5px;
    overflow-x: auto;
  }

  :deep(.message blockquote) {
    border-left: 3px solid #ddd;
    padding-left: 10px;
    color: #666;
    margin: 10px 0;
  }

  :deep(.message strong) {
    font-weight: bold;
  }

  :deep(.message em) {
    font-style: italic;
  }
  </style>
  
  <style>
  /* Markdown 样式 */
  .ai-message h1, .ai-message h2, .ai-message h3 {
    margin: 10px 0;
    font-weight: bold;
    word-break: break-word;
  }
  
  .ai-message h1 {
    font-size: 1.3em;
  }
  
  .ai-message h2 {
    font-size: 1.2em;
  }
  
  .ai-message h3 {
    font-size: 1.1em;
  }
  
  .ai-message ul, .ai-message ol {
    padding-left: 20px;
    margin: 10px 0;
  }
  
  .ai-message li {
    margin: 5px 0;
  }
  
  .ai-message p {
    margin: 8px 0;
    word-break: break-word;
    white-space: pre-wrap;
    display: block;
  }
  
  .ai-message code {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 2px 4px;
    border-radius: 3px;
    word-break: break-word;
  }
  
  .ai-message pre {
    background-color: rgba(0, 0, 0, 0.05);
    padding: 10px;
    border-radius: 5px;
    overflow-x: auto;
    white-space: pre-wrap;
    word-break: break-word;
  }
  
  .ai-message blockquote {
    border-left: 3px solid #ddd;
    padding-left: 10px;
    color: #666;
    margin: 10px 0;
  }
  
  .ai-message strong {
    font-weight: bold;
  }
  
  .ai-message em {
    font-style: italic;
  }
  </style>
  
  <style>
  /* 修复中文显示 */
  .message div {
    word-break: normal;
    word-wrap: break-word;
    overflow-wrap: break-word;
    text-align: left;
  }

  /* 改进拼音显示 */
  ruby {
    display: inline-flex;
    flex-direction: column;
    text-align: center;
    line-height: 1.5;
  }

  rt {
    font-size: 0.6em;
    line-height: 1.2;
    text-align: center;
    color: #666;
  }
  </style>
  
  <style>
  .child-name-selector {
    position: relative;
    width: 100%;
  }

  .create-child-form {
    margin-top: 15px;
    padding: 15px;
    background-color: #f8f9fe;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .child-form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 5px;
  }
  </style>
  
  

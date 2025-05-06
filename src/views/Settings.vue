<template>
  <div class="settings-page" style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);">
    <div class="close-button" @click="goBackToHome">
      <el-icon><Close /></el-icon>
    </div>
    <div class="setting-group">
      <h2>主题模式</h2>
      <el-radio-group v-model="themeMode">
        <el-radio label="light">浅色</el-radio>
        <el-radio label="dark">深色</el-radio>
      </el-radio-group>
    </div>
    <div class="setting-group">
      <h2>界面设置</h2>
      <div> <el-checkbox v-model="sidebarFixed">侧边栏固定</el-checkbox></div>
      <div class="textbox-style">
        <el-text style="color: #666; margin-right: 10px;">文本框样式</el-text>
        <el-radio-group v-model="messageBubbleShape">
          <el-radio label="rounded">圆角</el-radio>
          <el-radio label="square">直角</el-radio>
        </el-radio-group>
      </div>
      <div class="font-size-setting">
        <el-text style="color: #666; margin-right: 10px;">字体</el-text>
        <el-radio-group v-model="fontSize">
          <el-radio label="small">小</el-radio>
          <el-radio label="medium">中</el-radio>
          <el-radio label="large">大</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="setting-group">
      <h2>消息设置</h2>
      <div class="time-display-setting">
        <el-text style="color: #666; margin-right: 10px;">时间显示</el-text>
        <el-radio-group v-model="messageTimeFormat">
          <el-radio label="relative">相对</el-radio>
          <el-radio label="absolute">绝对</el-radio>
          <el-radio label="detailed">详细</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="setting-group">
      <h2>快捷键设置</h2>
      <div class="send-shortcut-setting">
        <el-text style="color: #666; margin-right: 10px;">发送消息</el-text>
        <el-radio-group v-model="sendMessageShortcut">
          <el-radio label="enter">Enter</el-radio>
          <el-radio label="ctrl+enter">Ctrl+Enter</el-radio>
          <el-radio label="shift+enter">Shift+Enter</el-radio>
        </el-radio-group>
      </div>
      <div class="new-session-shortcut-setting">
        <el-text style="color: #666; margin-right: 10px;">新建会话</el-text>
        <el-radio-group v-model="newSessionShortcut">
          <el-radio label="ctrl+n">Ctrl+N</el-radio>
          <el-radio label="alt+n">Alt+N</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="button-group">
      <el-button @click="restoreDefaults">恢复默认</el-button>
      <el-button type="primary" @click="saveSettings">保存设置</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue';
import { Close } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

// 默认设置
const defaultSettings = {
  themeMode: 'light',
  sidebarFixed: false,
  messageBubbleShape: 'rounded',
  fontSize: 'medium',
  messageTimeFormat: 'relative',
  autoScroll: true,
  sendMessageShortcut: 'enter',
  newSessionShortcut: 'ctrl+n'
};

// 获取本地存储中的设置
const storedSettings = JSON.parse(localStorage.getItem('settings')) || defaultSettings;

// 设置状态
const themeMode = ref(storedSettings.themeMode);
const sidebarFixed = ref(storedSettings.sidebarFixed);
const messageBubbleShape = ref(storedSettings.messageBubbleShape);
const fontSize = ref(storedSettings.fontSize);
const messageTimeFormat = ref(storedSettings.messageTimeFormat);
const autoScroll = ref(storedSettings.autoScroll);
const sendMessageShortcut = ref(storedSettings.sendMessageShortcut);
const newSessionShortcut = ref(storedSettings.newSessionShortcut);

const router = useRouter();

// 恢复默认设置
const restoreDefaults = () => {
  themeMode.value = defaultSettings.themeMode;
  sidebarFixed.value = defaultSettings.sidebarFixed;
  messageBubbleShape.value = defaultSettings.messageBubbleShape;
  fontSize.value = defaultSettings.fontSize;
  messageTimeFormat.value = defaultSettings.messageTimeFormat;
  autoScroll.value = defaultSettings.autoScroll;
  sendMessageShortcut.value = defaultSettings.sendMessageShortcut;
  newSessionShortcut.value = defaultSettings.newSessionShortcut;
};

// 保存设置
const saveSettings = () => {
  const settings = {
    themeMode: themeMode.value,
    sidebarFixed: sidebarFixed.value,
    messageBubbleShape: messageBubbleShape.value,
    fontSize: fontSize.value,
    messageTimeFormat: messageTimeFormat.value,
    autoScroll: autoScroll.value,
    sendMessageShortcut: sendMessageShortcut.value,
    newSessionShortcut: newSessionShortcut.value
  };
  localStorage.setItem('settings', JSON.stringify(settings));
  applySettings();
  alert('设置已保存');
};

// 应用设置
const applySettings = () => {
  // 应用主题模式
  if (themeMode.value === 'light') {
    document.body.classList.remove('dark-mode');
  } else {
    document.body.classList.add('dark-mode');
  }

  // 应用侧边栏固定设置
  const sidebar = document.querySelector('.sidebar');
  if (sidebar) {
    if (sidebarFixed.value) {
      sidebar.classList.add('fixed');
    } else {
      sidebar.classList.remove('fixed');
    }
  }

  // 应用文本框样式设置
  const messageBubbles = document.querySelectorAll('.message-bubble');
  messageBubbles.forEach((bubble) => {
    if (messageBubbleShape.value === 'rounded') {
      bubble.classList.add('rounded');
      bubble.classList.remove('square');
    } else {
      bubble.classList.add('square');
      bubble.classList.remove('rounded');
    }
  });

  // 应用字体大小设置
  if (fontSize.value === 'small') {
    document.documentElement.style.fontSize = '14px';
  } else if (fontSize.value === 'medium') {
    document.documentElement.style.fontSize = '18px';
  } else {
    document.documentElement.style.fontSize = '22px';
  }

  // 应用消息时间显示设置
  // 这里需要根据实际的消息时间显示逻辑进行调整
  console.log('消息时间显示设置为:', messageTimeFormat.value);

  // 应用自动滚动设置
  // 这里需要根据实际的消息列表滚动逻辑进行调整
  console.log('自动滚动设置为:', autoScroll.value);

  // 应用快捷键设置
  // 这里需要根据实际的快捷键监听逻辑进行调整
  console.log('发送消息快捷键设置为:', sendMessageShortcut.value);
  console.log('新建会话快捷键设置为:', newSessionShortcut.value);
};

// 页面加载时应用设置
onMounted(() => {
  applySettings();
});

// 监听设置变化并实时应用
watchEffect(() => {
  applySettings();
});

const goBackToHome = () => {
  router.push('/home');
};
</script>

<style scoped>
.settings-page {
  z-index: 1000;
}
.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
}
.setting-group {
  margin-bottom: 20px;
}
.button-group {
  margin-top: 20px;
}
.textbox-style, .font-size-setting, .time-display-setting, .send-shortcut-setting, .new-session-shortcut-setting {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
/* 深色模式样式 */
.dark-mode {
  background-color: #121212;
  color: white;
}
.dark-mode .el-radio-group {
  color: white;
}
.dark-mode .el-checkbox {
   color: white;
  }

.dark-mode .el-button {
  color: white;
  border-color: white;
}
.dark-mode .el-button.primary {
  background-color: #0066cc;
}
.dark-mode .el-text {
  color: #ccc;
}

/* 侧边栏固定样式 */
.sidebar.fixed {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
}

/* 消息气泡样式 */
.message-bubble.rounded {
  border-radius: 20px;
}
.message-bubble.square {
  border-radius: 0;
}
</style>
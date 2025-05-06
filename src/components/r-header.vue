<template>
  <div class="header-container">
    <div class="header-left">
      欢迎登录互动故事书系统！
    </div>

    <el-button  type="success" plain @click="clickJudge">
      AI对话
    </el-button>

    <div class="user-info">
      &nbsp;&nbsp;
      <el-icon><User /></el-icon>
      <span class="user-name">你好：{{ user.username }}</span>
    </div>

    <el-popconfirm
      confirmButtonText="好的"
      cancelButtonText="不用了"
      icon="el-icon-info"
      icon-color="red"
      title="确定退出登录吗？"
      @confirm="handleLogout"
    >
      <template #reference>
        <el-button type="danger" class="logout-button" round>
          退出登录
        </el-button>
      </template>
    </el-popconfirm>

    <!-- 设置按钮 -->
    <el-button type="success" class="settings-button" @click="goToSettings">
      <el-icon><Setting /></el-icon>
      设置
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { User, Setting } from '@element-plus/icons-vue';
import { ElIcon } from 'element-plus';

const router = useRouter();
const user = ref({});
const isAdmin = ref(false);

onMounted(() => {
  // 从本地存储获取用户信息
  const userStr = localStorage.getItem('user');
  if (userStr) {
    user.value = JSON.parse(userStr);
    // 检查是否为管理员
    isAdmin.value = user.value.loginAccount === 'admin';
  }
});

const clickJudge = () => {
  router.push('/chat');
};

const handleLogout = () => {
  localStorage.removeItem('user'); // 清除用户信息
  router.push('/login');
};

const goToSettings = () => {
  router.push('/MyInfo');
};
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #333;
  color: white;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
}
.user-name {
  font-size: 18px;
  color: white; /* 确保在深色背景下可见 */
}
.logout-button {
  margin-left: auto;
}
.settings-button {
  background-color: transparent;
  border: none;
  color: white;
  font-size: 18px;
}
</style>
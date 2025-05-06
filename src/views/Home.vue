<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1>欢迎使用<span class="highlight">互动故事书</span>系统</h1>
        <p class="subtitle">为3-6岁儿童量身打造的智能互动学习平台</p>
      </div>
      <div class="banner-image">
        <img src="../assets/banner-kids.png" alt="儿童阅读插图" onerror="this.src='https://img.freepik.com/free-vector/kids-reading-concept-illustration_114360-8513.jpg?w=900&t=st=1689153698~exp=1689154298~hmac=b0c5de3c09ae1c9fe8bd9f1bea7d09b86cb13148dbdf8029dcbfb5efb4c5eeaa'; this.onerror=null;" />
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card" v-if="user">
      <div class="card-header">
        <div class="avatar">
          <img :src="userAvatar" alt="用户头像">
        </div>
        <div class="user-greeting">
          <h2>你好，{{ user.userName || '亲爱的用户' }}</h2>
          <p>欢迎回来！今天是个讲故事的好日子 🌞</p>
        </div>
      </div>
      <div class="card-body">
        <div class="info-item">
          <i class="el-icon-user"></i>
          <span>账号: {{ user.username || '未设置' }}</span>
        </div>
        <div class="info-item">
          <i class="el-icon-message"></i>
          <span>邮箱: {{ user.email || '未设置' }}</span>
        </div>
        <div class="info-item">
          <i class="el-icon-phone"></i>
          <span>电话: {{ user.phone || '未设置' }}</span>
        </div>
        <div class="info-item" v-if="storyStats">
          <i class="el-icon-reading"></i>
          <span>故事记录: {{ storyStats.count || 0 }}个</span>
        </div>
      </div>
      <div class="card-footer">
        <el-button type="primary" @click="goToStory">开始故事</el-button>
        <el-button type="info" @click="goToHistory">查看历史</el-button>
        <el-button type="danger" @click="logout">退出登录</el-button>
      </div>
    </div>

    <!-- 功能模块展示 -->
    <div class="features-section">
      <h2 class="section-title">学习成长两大模块</h2>
      <div class="features-grid">
        <div class="feature-card life-education">
          <div class="feature-icon">
            <i class="el-icon-toilet-paper"></i>
          </div>
          <h3>生活教育</h3>
          <ul>
            <li>饮食卫生</li>
            <li>作息规律</li>
            <li>生活自理</li>
            <li>安全自我保护</li>
          </ul>
          <el-button type="success" @click="startStoryWithType('生活教育')">探索生活教育</el-button>
        </div>
        <div class="feature-card learning-growth">
          <div class="feature-icon">
            <i class="el-icon-notebook-2"></i>
          </div>
          <h3>学习成长</h3>
          <ul>
            <li>阅读倾听</li>
            <li>交流表达</li>
            <li>益智教育</li>
            <li>艺术启发</li>
          </ul>
          <el-button type="primary" @click="startStoryWithType('学习成长')">探索学习成长</el-button>
        </div>
      </div>
    </div>

    <!-- 最近故事记录 -->
    <div class="recent-stories" v-if="recentStories && recentStories.length > 0">
      <h2 class="section-title">最近故事</h2>
      <div class="stories-carousel">
        <div v-for="story in recentStories" :key="story.id" class="story-card" @click="viewStoryDetail(story.id)">
          <div class="story-type-badge" :class="story.storyType === '生活教育' ? 'education' : 'learning'">
            {{ story.storyType }}
          </div>
          <h3>{{ story.storyTheme }}</h3>
          <div class="story-meta">
            <div class="meta-item">
              <i class="el-icon-user"></i>
              <span>{{ story.childName }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-date"></i>
              <span>{{ formatDate(story.createTime) }}</span>
            </div>
          </div>
          <el-button type="text" @click="viewStoryDetail(story.id)">查看详情</el-button>
        </div>
      </div>
    </div>

    <!-- 关于系统 -->
    <div class="about-section">
      <h2 class="section-title">关于互动故事书系统</h2>
      <p>互动故事书是一个面向3-6岁学龄前儿童的智能学习平台，通过大模型生成的互动故事，将儿童置于故事场景中，根据用户输入调整故事走向，支持语音交互。</p>
      <div class="about-features">
        <div class="about-feature">
          <i class="el-icon-mic"></i>
          <span>语音交互</span>
        </div>
        <div class="about-feature">
          <i class="el-icon-chat-dot-square"></i>
          <span>故事生成</span>
        </div>
        <div class="about-feature">
          <i class="el-icon-connection"></i>
          <span>互动体验</span>
        </div>
        <div class="about-feature">
          <i class="el-icon-data-line"></i>
          <span>学习跟踪</span>
        </div>
      </div>
    </div>

    <!-- 故事详情对话框 -->
    <el-dialog title="故事详情" :visible.sync="dialogVisible" width="70%">
      <div v-if="storyDetail" class="story-detail">
        <div class="detail-header">
          <h3>{{ storyDetail.storyTheme }}</h3>
          <div class="detail-meta">
            <span>{{ storyDetail.storyType }}</span>
            <span>{{ formatDate(storyDetail.createTime) }}</span>
            <span>时长: {{ formatDuration(storyDetail.duration) }}</span>
          </div>
        </div>
        <div class="message-list">
          <div 
            v-for="(message, index) in storyDetail.contentList" 
            :key="index"
            :class="['message-item', message.speaker === 'ai' ? 'ai-message' : 'user-message']"
          >
            <div class="message-avatar">
              <img :src="message.speaker === 'ai' ? aiAvatar : userAvatar" :alt="message.speaker">
            </div>
            <div class="message-content">
              <div class="message-bubble">{{ message.content }}</div>
              <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="loading-detail">
        <el-loading></el-loading>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const router = useRouter()
const user = ref(null)
const recentStories = ref([])
const storyStats = ref(null)
const aiAvatar = ref('https://api.dicebear.com/7.x/bottts/svg?seed=storybot')
const userAvatar = computed(() => {
  return user.value?.avatar || `https://api.dicebear.com/7.x/adventurer/svg?seed=${user.value?.username || 'user'}`
})

const dialogVisible = ref(false)
const storyDetail = ref(null)

onMounted(async () => {
  // 从localStorage获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      user.value = JSON.parse(userStr)
      console.log('Home页面获取到的用户信息:', user.value)
      
      // 获取最近故事
      if(user.value.id) {
        await fetchRecentStories()
        await fetchStoryStats()
      }
    } catch (e) {
      console.error('解析用户信息失败', e)
    }
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})

// 获取最近故事
const fetchRecentStories = async () => {
  try {
    const { data } = await axios.get('/api/story/history', { 
      params: { 
        userId: user.value.id,
        limit: 5 
      } 
    })
    if (data.code === 200) {
      recentStories.value = data.data || []
    }
  } catch (error) {
    console.error('获取最近故事失败:', error)
  }
}

// 获取故事统计
const fetchStoryStats = async () => {
  try {
    const { data } = await axios.get('/api/story/stats', { 
      params: { userId: user.value.id } 
    })
    if (data.code === 200) {
      storyStats.value = data.data
    }
  } catch (error) {
    console.error('获取故事统计失败:', error)
  }
}

// 查看故事详情
const viewStoryDetail = async (recordId) => {
  dialogVisible.value = true
  storyDetail.value = null
  
  try {
    const { data } = await axios.get(`/api/story/detail/${recordId}`)
    if (data.code === 200) {
      storyDetail.value = data.data
    } else {
      ElMessage.error(data.message || '获取故事详情失败')
    }
  } catch (error) {
    console.error('获取故事详情失败:', error)
    ElMessage.error('获取故事详情失败')
  }
}

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return '-'
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '0分钟'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 1) return '不到1分钟'
  return `${minutes}分钟`
}

// 格式化消息时间
const formatMessageTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 退出登录
const logout = () => {
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 开始故事
const goToStory = () => {
  router.push('/chat')
}

// 查看历史
const goToHistory = () => {
  router.push('/history')
}

// 根据类型开始故事
const startStoryWithType = (type) => {
  localStorage.setItem('selectedStoryType', type)
  router.push('/chat')
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
}

/* 欢迎横幅 */
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.banner-content {
  flex: 1;
  padding-right: 20px;
}

.banner-content h1 {
  font-size: 2.5rem;
  margin: 0;
  color: #344767;
}

.highlight {
  color: #3E78F3;
  position: relative;
}

.subtitle {
  font-size: 1.2rem;
  margin-top: 10px;
  color: #5e6e82;
}

.banner-image {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.banner-image img {
  max-width: 100%;
  max-height: 250px;
  object-fit: contain;
  border-radius: 8px;
}

/* 用户卡片 */
.user-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  background-color: #f0f0f0;
  border: 3px solid #3E78F3;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-greeting h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #344767;
}

.user-greeting p {
  margin: 5px 0 0;
  color: #5e6e82;
}

.card-body {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  color: #5e6e82;
}

.info-item i {
  margin-right: 8px;
  font-size: 1.2rem;
  color: #3E78F3;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 功能模块展示 */
.features-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #344767;
  position: relative;
  padding-bottom: 10px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background-color: #3E78F3;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.feature-card {
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.life-education {
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
  border-left: 5px solid #67C23A;
}

.learning-growth {
  background: linear-gradient(135deg, #ecf5ff 0%, #dbecff 100%);
  border-left: 5px solid #409EFF;
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
  color: #344767;
}

.feature-card h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #344767;
}

.feature-card ul {
  margin-top: 0;
  margin-bottom: 20px;
  padding-left: 20px;
  color: #5e6e82;
}

.feature-card li {
  margin-bottom: 5px;
}

/* 最近故事 */
.recent-stories {
  margin-bottom: 30px;
}

.stories-carousel {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  overflow-x: auto;
  padding: 5px;
}

.story-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  position: relative;
  transition: all 0.3s;
  cursor: pointer;
}

.story-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.story-type-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  color: white;
}

.story-type-badge.education {
  background-color: #67C23A;
}

.story-type-badge.learning {
  background-color: #409EFF;
}

.story-card h3 {
  margin-top: 5px;
  margin-bottom: 15px;
  color: #344767;
}

.story-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
  color: #5e6e82;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item i {
  margin-right: 5px;
}

/* 关于部分 */
.about-section {
  background-color: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.about-section p {
  color: #5e6e82;
  line-height: 1.6;
  margin-bottom: 20px;
}

.about-features {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: space-between;
  margin-top: 20px;
}

.about-feature {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  min-width: 120px;
}

.about-feature i {
  font-size: 2rem;
  margin-bottom: 10px;
  color: #3E78F3;
}

/* 故事详情对话框 */
.story-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-header h3 {
  margin-bottom: 10px;
  color: #303133;
}

.detail-meta {
  display: flex;
  gap: 15px;
  color: #606266;
  font-size: 14px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  display: flex;
  gap: 15px;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-bubble {
  background-color: #f5f7fa;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 400px;
  word-break: break-word;
}

.ai-message .message-bubble {
  background-color: #f0f9eb;
}

.user-message .message-bubble {
  background-color: #ecf5ff;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
  }
  
  .banner-content {
    padding-right: 0;
    margin-bottom: 20px;
  }
  
  .card-body {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style> 
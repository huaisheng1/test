<template>
  <div class="history-container">
    <div class="history-header">
      <h1>故事历史记录</h1>
      <div class="filters">
        <el-select v-model="storyType" placeholder="故事类型" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="生活教育" value="生活教育"></el-option>
          <el-option label="学习成长" value="学习成长"></el-option>
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD">
        </el-date-picker>
        <el-button type="primary" @click="fetchStoryRecords">查询</el-button>
      </div>
    </div>

    <!-- 故事列表页面 -->
    <div v-if="!showDetailView" class="history-content">
      <div v-if="loading" class="loading">
        <el-loading></el-loading>
      </div>
      <div v-else-if="storyRecords.length === 0" class="no-data">
        <p>暂无故事记录</p>
      </div>
      <div v-else class="history-list">
        <div 
          v-for="record in storyRecords" 
          :key="record.id" 
          class="history-card"
          @click="viewStoryDetail(record.id)"
        >
          <div class="card-header">
            <div class="story-theme">{{ record.storyTheme }}</div>
            <div class="story-type" :class="record.storyType === '生活教育' ? 'education' : 'learning'">
              {{ record.storyType }}
            </div>
          </div>
          <div class="card-content">
            <div class="story-info">
              <div class="info-item">
                <i class="el-icon-user"></i>
                <span>{{ record.childName }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-time"></i>
                <span>{{ formatDuration(record.duration) }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-date"></i>
                <span>{{ formatDate(record.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 故事详情页面 -->
    <div v-else class="story-detail-page">
      <div class="detail-header">
        <el-button icon="el-icon-back" @click="goBackToList">返回列表</el-button>
        <h2 v-if="storyDetail">{{ storyDetail.storyTheme }}</h2>
      </div>
      
      <div v-if="detailLoading" class="loading-detail">
        <el-loading></el-loading>
      </div>
      <div v-else-if="storyDetail" class="detail-container">
        <div class="detail-info">
          <div class="info-card">
            <div class="info-item">
              <label>故事类型：</label>
              <span>{{ storyDetail.storyType }}</span>
            </div>
            <div class="info-item">
              <label>小朋友：</label>
              <span v-if="storyDetail.child">{{ storyDetail.child.name }}</span>
            </div>
            <div class="info-item">
              <label>时长：</label>
              <span>{{ formatDuration(storyDetail.duration) }}</span>
            </div>
            <div class="info-item">
              <label>创建时间：</label>
              <span>{{ formatDate(storyDetail.createTime) }}</span>
            </div>
          </div>
        </div>
        
        <div class="chat-container">
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
                <div class="message-bubble" v-html="formatContent(message.content)"></div>
                <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-data">
        <p>无法加载故事详情，请稍后再试</p>
      </div>
    </div>

    <!-- Dialog for story detail (for backwards compatibility) -->
    <el-dialog 
      title="故事详情" 
      :visible.sync="dialogVisible" 
      width="70%" 
      :before-close="handleClose"
    >
      <div v-if="storyDetail" class="story-detail">
        <div class="detail-header">
          <h2>{{ storyDetail.storyTheme }}</h2>
          <div class="detail-info">
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
              <div class="message-bubble" v-html="formatContent(message.content)"></div>
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

<script>
import axios from '../utils/axios'
import userAvatarImg from '@/assets/user.jpg'
import aiAvatarImg from '@/assets/ai.jpg'

export default {
  name: 'HistoryView',
  props: {
    id: {
      type: String,
      required: false
    }
  },
  data() {
    return {
      loading: false,
      storyRecords: [],
      storyType: '',
      dateRange: null,
      dialogVisible: false,
      storyDetail: null,
      detailLoading: false,
      userAvatar: userAvatarImg,
      aiAvatar: aiAvatarImg,
      // 从localStorage获取用户信息
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      // 显示详情视图
      showDetailView: false
    }
  },
  created() {
    this.checkRouteParams()
  },
  watch: {
    // 监听路由变化
    '$route'(to, from) {
      this.checkRouteParams()
    }
  },
  methods: {
    checkRouteParams() {
      const recordId = this.$route.params.id
      if (recordId) {
        // 显示详情页
        this.showDetailView = true
        this.loadStoryDetail(recordId)
      } else {
        // 显示列表页
        this.showDetailView = false
        this.fetchStoryRecords()
      }
    },
    async fetchStoryRecords() {
      if (!this.user || !this.user.id) {
        this.$message.error('请先登录')
        this.$router.push('/login')
        return
      }

      this.loading = true
      try {
        // 构建查询参数
        const params = {
          userId: this.user.id
        }
        if (this.storyType) {
          params.storyType = this.storyType
        }
        if (this.dateRange && this.dateRange[0] && this.dateRange[1]) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }

        const { data } = await axios.get('/api/story/history', { params })
        if (data.code === 200) {
          this.storyRecords = data.data || []
        } else {
          this.$message.error(data.message || '获取历史记录失败')
        }
      } catch (error) {
        console.error('获取历史记录失败:', error)
        this.$message.error('获取历史记录失败')
      } finally {
        this.loading = false
      }
    },
    viewStoryDetail(recordId) {
      // 通过路由导航
      this.$router.push(`/history/${recordId}`)
    },
    goBackToList() {
      this.$router.push('/history')
    },
    async loadStoryDetail(recordId) {
      this.detailLoading = true
      this.storyDetail = null

      try {
        const { data } = await axios.get(`/api/story/detail/${recordId}`)
        if (data.code === 200) {
          this.storyDetail = data.data
        } else {
          this.$message.error(data.message || '获取故事详情失败')
        }
      } catch (error) {
        console.error('获取故事详情失败:', error)
        this.$message.error('获取故事详情失败')
      } finally {
        this.detailLoading = false
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.storyDetail = null
    },
    formatDate(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    formatDuration(seconds) {
      if (!seconds) return '0分钟'
      const minutes = Math.floor(seconds / 60)
      if (minutes < 1) return '不到1分钟'
      return `${minutes}分钟`
    },
    formatMessageTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    formatContent(content) {
      // 处理文本格式，如果需要可以实现更复杂的格式化
      if (!content) return '';
      
      // 基本的换行处理
      return content
        .replace(/\n/g, '<br>')
        .replace(/\r/g, '<br>');
    }
  }
}
</script>

<style scoped>
.history-container {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.history-header {
  margin-bottom: 30px;
}

.history-header h1 {
  color: #409EFF;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.history-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.history-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  overflow: hidden;
}

.history-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px 0 rgba(0, 0, 0, 0.2);
}

.card-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.story-theme {
  font-weight: bold;
  font-size: 16px;
}

.story-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  color: white;
}

.education {
  background-color: #FF9800;
}

.learning {
  background-color: #4CAF50;
}

.card-content {
  padding: 15px;
}

.story-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.info-item i {
  margin-right: 8px;
}

.loading, .no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.no-data p {
  color: #909399;
  font-size: 16px;
}

/* 故事详情样式 */
.story-detail-page {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  min-height: 70vh;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeaea;
}

.detail-header h2 {
  margin: 0 0 0 20px;
  color: #303133;
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-info {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.info-card {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.info-card .info-item {
  display: flex;
  gap: 10px;
}

.info-card label {
  font-weight: bold;
  color: #606266;
}

.chat-container {
  background-color: #f9fafc;
  border-radius: 8px;
  padding: 20px;
  min-height: 400px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.ai-message {
  align-self: flex-start;
}

.user-message {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-bubble {
  padding: 12px 15px;
  border-radius: 8px;
  word-break: break-word;
}

.ai-message .message-bubble {
  background-color: white;
  color: #333;
  border: 1px solid #e8e8e8;
}

.user-message .message-bubble {
  background-color: #409EFF;
  color: white;
}

.message-time {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  align-self: flex-end;
}

/* 确保对话框内的样式与页面版本一致 */
:deep(.el-dialog__body) {
  padding: 20px;
}

.loading-detail {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style>

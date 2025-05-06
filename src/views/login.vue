<template>
    <div class="login">
      <div class="login1">
      </div>
      <div class="login4">
        <div class="login5">
          <div style="color: #d37136" class="login6">
            欢迎使用互动故事书系统
          </div>
          <el-input prefix-icon="el-icon-user" v-model="loginAccount" placeholder="请输入用户名"></el-input>
          <el-input prefix-icon="el-icon-star-off" type="password" v-model="password" placeholder="请输入密码"></el-input>
          <div class="login7" @click="toForget">
            忘记密码？
          </div>
  
          <div class="login8" @click="login">
            登 录
          </div>
          <div class="login8" @click="toRegister">
            注 册
          </div>
        </div>
      </div>
  
    </div>
  </template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

// 表单数据
const loginAccount = ref('')
const password = ref('')
const router = useRouter()

// 登录方法
const login = async () => {
  if (loginAccount.value === '') {
    ElMessage({
      message: '请输入用户名',
      type: 'warning'
    })
    return
  }
  if (password.value === '') {
    ElMessage({
      message: '请输入密码',
      type: 'warning'
    })
    return
  }
  
  try {
    const { data } = await axios.post('/api/user/login', {
      username: loginAccount.value,
      password: password.value
    })
    
    if (data.code === 200) {
      const userData = data.data
      
      // 打印调试信息，观察返回的数据结构
      console.log('登录成功，返回数据：', userData)
      
      // 存储用户数据到localStorage
      localStorage.setItem('user', JSON.stringify(userData))
      
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
      ElMessage.error(data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error(error.response?.data?.message || '登录失败')
  }
}

// 页面跳转
const toRegister = () => {
  router.push('/register')
}

const toForget = () => {
  router.push('/forget')
}
</script>


<style scoped>
.login {
    width: 100%;
    height: 100%;
    font-family: '黑体';
    display: flex;
}
.login1 {
    width: 60%;
    height: 100%;
    background-image: url('../assets/login.png');
    background-size: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}
.login2 {
    font-size: 35px;
    font-weight: bold;
}
.login3 {
    margin-top: 10px;
    letter-spacing: 2px;
    font-size: 20px;
    font-weight: bold;
}
.login4 {
    width: 40%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
.login5 {
    width: 80%;
    height: 80%;
    flex-direction: column;
    display: flex;
    justify-content: space-around;
    align-items: center;
}
.login6 {
    font-size: 30px;
    font-weight: bold;
}
.login7 {
    width: 100%;
    text-align: right;
    cursor: pointer;
}
.login8 {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #ffffff;
    width: 70%;
    height: 50px;
    cursor: pointer;
    border-radius: 20px;
    background-color: #3E78F3;
}
</style>
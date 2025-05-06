<template>
  <div class="login">
    <div class="login1">
    </div>
    <div class="login4">
        <div class="login5">
            <div style="color: #d37136" class="login6">
            注册
          </div>
          <el-input prefix-icon="el-icon-user" v-model="loginAccount" placeholder="请输入用户名"></el-input>
            <el-input prefix-icon="el-icon-star-off" type="password" v-model="password" placeholder="请输入密码"></el-input>
            <el-input prefix-icon="el-icon-s-order" v-model="email" placeholder="请输入邮箱"></el-input>
            <el-input prefix-icon="el-icon-s-ticket" v-model="tel" placeholder="请输入手机号"></el-input>
          <div class="login8" @click="saveUser">
                注册
            </div>
            <div class="login8" @click="toLogin">
                登 录
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

// 表单数据
const loginAccount = ref('')
const password = ref('')
const email = ref('')
const tel = ref('')

const router = useRouter()

// 注册方法
const saveUser = async () => {
  // 表单验证
  if (!loginAccount.value) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!password.value) {
    ElMessage.warning('请输入密码')
    return
  }
  
  // 调用注册接口
  try {
    const { data } = await axios.post('/api/user/register', {
      username: loginAccount.value,
      password: password.value,
      email: email.value,
      phone: tel.value
    })
    
    if (data.code === 200) {
      const userData = data.data
      
      // 打印调试信息，观察返回的数据结构
      console.log('注册成功，返回数据：', userData)
      
      // 存储用户数据到localStorage
      localStorage.setItem('user', JSON.stringify(userData))
      
      ElMessage.success('注册成功')
      router.push('/home')
    } else {
      ElMessage.error(data.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败：', error)
    ElMessage.error(error.response?.data?.message || '注册失败')
  }
}

// 页面跳转
const toLogin = () => {
  router.push('/login')
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

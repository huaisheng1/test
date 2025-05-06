<template>
  <div class="login">
    <div class="login1">
    </div>
    <div class="login4">
        <div class="login5">
          <div style="color: #d37136" class="login6">
            重置密码
          </div>
          <el-input prefix-icon="el-icon-user" v-model="loginAccount" placeholder="请输入用户名"></el-input>
          <el-input prefix-icon="el-icon-s-order" v-model="email" placeholder="请输入邮箱"></el-input>
          <el-input prefix-icon="el-icon-star-off" type="password" v-model="password" placeholder="请输入新密码"></el-input>
          
          <div class="login8" @click="forgetPassword">
              提交
          </div>
          <div class="login8" @click="toLogin">
              返回登录
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
const email = ref('')
const password = ref('')

const router = useRouter()

// 重置密码方法
const forgetPassword = async () => {
  // 表单验证
  if (!loginAccount.value) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!email.value) {
    ElMessage.warning('请输入邮箱')
    return
  }
  if (!password.value) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  // 调用重置密码接口
  try {
    const { data } = await axios.post('/api/user/resetPassword', {
      username: loginAccount.value,
      email: email.value,
      password: password.value
    })
    
    if (data.code === 200) {
      ElMessage.success(data.message || '密码重置成功')
      router.push('/login')
    } else {
      ElMessage.error(data.message || '密码重置失败')
    }
  } catch (error) {
    console.error('密码重置失败：', error)
    ElMessage.error(error.response?.data?.message || '密码重置失败')
  }
}

// 跳转到登录页
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

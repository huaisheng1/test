<template>
  <div>
    <el-container>
      <el-header
        style="
          height: 40px;
          text-align: right;
          font-size: 18px;
          background-color: #252F42;
          border-radius: 3px;
        "
      >
        <rHeader></rHeader>
      </el-header>
      <el-container>
        <el-main class="main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import rHeader from '@/components/r-header.vue'
</script>

<style scoped>
.main {
  background: url('src/assets/background.png') no-repeat center center fixed;
  background-size: cover; /* 确保背景图按比例缩放并覆盖容器 */
  height: 100vh; /* 确保背景图覆盖整个视口 */
}
</style>

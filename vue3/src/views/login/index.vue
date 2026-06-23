<script setup lang="ts">
import { loginAPI } from '@/api/employee'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/store'

const router = useRouter()
const userInfoStore = useUserInfoStore()

const form = ref({
  account: '',
  password: ''
})

const loginRef = ref()

const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]{1,10}$/, message: '1-10位字母或数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '6-15位非空字符', trigger: 'blur' }
  ]
}

const loginFn = async () => {
  const valid = await loginRef.value.validate()
  if (!valid) return

  const { data: res } = await loginAPI(form.value)

  if (res.code !== 0) return

  ElMessage.success('登录成功')

  userInfoStore.userInfo = res.data

  router.push('/')
}
</script>

<template>
  <div class="login-page">

    <!-- 背景 -->
    <div class="bg"></div>

    <!-- 遮罩（提高可读性） -->
    <div class="mask"></div>

    <!-- 登录卡片 -->
    <div class="login-card">

      <!-- logo -->
      <div class="logo">
        <img src="@/assets/image/logo.png" />
      </div>

      <div class="title">外卖后台管理系统</div>
      <div class="sub">Take-out Management System</div>

      <el-form
        ref="loginRef"
        :model="form"
        :rules="rules"
        class="form"
      >
        <el-form-item prop="account">
          <el-input
            v-model="form.account"
            placeholder="请输入账号"
            size="large"
            class="input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            size="large"
            class="input"
          />
        </el-form-item>

        <el-button
          type="primary"
          class="btn"
          @click="loginFn"
        >
          登录
        </el-button>

        <div class="link">
          <el-link type="primary" @click="$router.push('/reg')">
            还没有账号？立即注册
          </el-link>
        </div>
      </el-form>

    </div>
  </div>
</template>

<style scoped lang="less">

/* 整体页面 */
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: "Microsoft YaHei", Arial;
}

/* 背景图 */
.bg {
  position: absolute;
  inset: 0;
  background: url('@/assets/image/login.jpg') center/cover no-repeat;
  transform: scale(1.05);
}

/* 黑色遮罩（重点：提高文字清晰度） */
.mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 2;

  width: 380px;
  padding: 36px 32px;

  border-radius: 12px;

  /* 轻玻璃，但不影响阅读 */
  background: rgba(255, 255, 255, 0.10);
  backdrop-filter: blur(14px);

  border: 1px solid rgba(255, 255, 255, 0.18);

  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.45);

  text-align: center;
}

/* logo */
.logo img {
  width: 64px;
  margin-bottom: 10px;
}

/* 标题（提高对比） */
.title {
  font-size: 22px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 6px;
}

/* 副标题 */
.sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 18px;
}

/* 输入框优化（清晰边界） */
.input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: none;
  border-radius: 8px;
}

.input :deep(input) {
  color: #222;
  font-size: 14px;
}

/* 登录按钮 */
.btn {
  width: 100%;
  height: 40px;
  font-weight: 600;
  margin-top: 8px;
}

/* 注册链接 */
.link {
  margin-top: 14px;
}

/* 响应式 */
@media (max-width: 500px) {
  .login-card {
    width: 90%;
    padding: 28px 20px;
  }
}
</style>
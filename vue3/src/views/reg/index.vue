<script setup lang="ts" name="my-register">
import { registerAPI } from '@/api/employee'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  account: '',
  password: '',
  repassword: ''
})

const registerRef = ref()

const samePwd = (rules: any, value: any, callback: any) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  account: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9]{1,10}$/,
      message: '1-10位字母或数字',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '6-15位非空字符',
      trigger: 'blur'
    }
  ],
  repassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '6-15位非空字符',
      trigger: 'blur'
    },
    { validator: samePwd, trigger: 'blur' }
  ]
}

const router = useRouter()

const registerFn = async () => {
  const valid = await registerRef.value.validate()
  if (!valid) return

  const { data: res } = await registerAPI(form.value)

  if (res.code !== 0) return

  ElMessage.success('注册成功!')

  router.push('/login')
}
</script>

<template>
  <div class="register-page">

    <!-- 背景 -->
    <div class="bg"></div>

    <!-- 遮罩（统一登录风格） -->
    <div class="mask"></div>

    <!-- 卡片 -->
    <div class="register-card">

      <div class="logo">
        <img src="@/assets/image/logo.png" />
      </div>

      <div class="title">外卖后台管理系统</div>
      <div class="sub">Create your account</div>

      <el-form
        ref="registerRef"
        :model="form"
        :rules="rules"
        class="form"
      >
        <el-form-item prop="account">
          <el-input
            v-model="form.account"
            placeholder="请输入用户名"
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

        <el-form-item prop="repassword">
          <el-input
            v-model="form.repassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            size="large"
            class="input"
          />
        </el-form-item>

        <el-button
          type="primary"
          class="btn"
          @click="registerFn"
        >
          注册
        </el-button>

        <div class="link">
          <el-link type="primary" @click="router.push('/login')">
            已有账号？去登录
          </el-link>
        </div>

      </el-form>

    </div>
  </div>
</template>

<style scoped lang="less">

.register-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: "Microsoft YaHei", Arial;
}

/* 背景 */
.bg {
  position: absolute;
  inset: 0;
  background: url('@/assets/image/reg.jpg') center/cover no-repeat;
  transform: scale(1.05);
}

/* 遮罩（统一登录页风格） */
.mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
}

/* 卡片 */
.register-card {
  position: relative;
  z-index: 2;

  width: 380px;
  padding: 36px 32px;

  border-radius: 12px;

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

/* 标题 */
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

/* 输入框统一风格 */
.input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: none;
  border-radius: 8px;
}

.input :deep(input) {
  color: #222;
  font-size: 14px;
}

/* 按钮 */
.btn {
  width: 100%;
  height: 40px;
  font-weight: 600;
  margin-top: 8px;
}

/* 链接 */
.link {
  margin-top: 14px;
}

/* 响应式 */
@media (max-width: 500px) {
  .register-card {
    width: 90%;
    padding: 28px 20px;
  }
}
</style>
<script setup lang="ts" name="layout">
import { RouterView, useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/store'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { fixPwdAPI } from '@/api/employee'
import { getStatusAPI, fixStatusAPI } from '@/api/shop'
import { ElNotification } from 'element-plus'

// ------ data ------
const dialogFormVisible = ref(false)
const dialogStatusVisible = ref(false)
const formLabelWidth = '80px'
const isCollapse = ref(false)

const menuList = [
  {
    title: '控制台',
    path: '/dashboard',
    icon: 'pieChart',
  },
  {
    title: '数据统计',
    path: '/statistics',
    icon: 'memo',
  },
  {
    title: '订单管理',
    path: '/order',
    icon: 'collection',
  },
  {
    title: '分类管理',
    path: '/category',
    icon: 'postcard',
  },
  {
    title: '套餐管理',
    path: '/setmeal',
    icon: 'user',
  },
  {
    title: '菜品管理',
    path: '/dish',
    icon: 'dish',
  },
  {
    title: '员工管理',
    path: '/employee',
    icon: 'setting',
  },
]

const form = reactive({
  oldPwd: '',
  newPwd: '',
  rePwd: '',
})
const pwdRef = ref()
const status = ref(1)
const status_active = ref(1) // 单选框绑定的动态值

// 自定义校验规则: 两次密码是否一致
const samePwd = (rules: any, value: any, callback: any) => {
  if (value !== form.newPwd) {
    // 如果验证失败，则调用 回调函数时，指定一个 Error 对象。
    callback(new Error('两次输入的密码不一致!'))
  } else {
    // 如果验证成功，则直接调用 callback 回调函数即可。
    callback()
  }
}
const rules = { // 表单的规则检验对象
  oldPwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9]{1,10}$/,
      message: '原密码必须是1-10的大小写字母数字',
      trigger: 'blur'
    }
  ],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '新密码必须是6-15的非空字符', trigger: 'blur' }
  ],
  rePwd: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '新密码必须是6-15的非空字符', trigger: 'blur' },
    { validator: samePwd, trigger: 'blur' }
  ]
}

// ------ method ------
const router = useRouter()
const userInfoStore = useUserInfoStore()
const route = useRoute();
// 根据当前路由的路径返回要激活的菜单项
const getActiveAside = () => {
  console.log('当前路由的路径--------------', route.path)
  return route.path;
};

// 初始化时获取营业状态
const init = async () => {
  const { data: res } = await getStatusAPI()
  console.log('初始化后的status status_active', res.data)
  status.value = res.data
  status_active.value = res.data
}
init()

// 关闭修改店铺状态对话框
const cancelStatus = () => {
  ElMessage({
    type: 'info',
    message: '已取消修改',
  })
  dialogStatusVisible.value = false
}
// 关闭修改密码对话框
const cancelForm = () => {
  ElMessage({
    type: 'info',
    message: '已取消修改',
  })
  dialogFormVisible.value = false
}
const fixStatus = async () => {
  const { data: res } = await fixStatusAPI(status_active.value)

  if (res.code !== 0) return

  status.value = status_active.value

  ElMessage.success('修改成功')

  dialogStatusVisible.value = false
}
// 修改密码
const fixPwd = async () => {
  const valid = await pwdRef.value.validate()
  if (valid) {
    const submitForm = {
      oldPwd: form.oldPwd,
      newPwd: form.newPwd,
    }
    console.log('要提交的表单信息')
    console.log(submitForm)
    const { data: res } = await fixPwdAPI(submitForm)
    if (res.code != 0) return   // 密码错误信息会在相应拦截器中捕获并提示
    ElMessage({
      type: 'success',
      message: '修改成功',
    })
    dialogFormVisible.value = false
  } else {
    return false
  }
}

const quitFn = () => {
  // 为了让用户体验更好，来个确认提示框
  ElMessageBox.confirm(
    '走了，爱是会消失的吗?',
    '退出登录',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(() => {
      ElMessage({
        type: 'success',
        message: '退出成功',
      })
      // 清除用户信息，包括token
      userInfoStore.userInfo = null
      console.log(userInfoStore)
      router.push('/login')
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消退出',
      })
    })
}

// refs
const websocket = ref<WebSocket | null>(null)
const shopShow = ref(false)

const audio1 = ref<HTMLAudioElement | null>(null)
const audio2 = ref<HTMLAudioElement | null>(null)

const webSocket = () => {
  const clientId = Math.random().toString(36).slice(2)
  const socketUrl = 'ws://localhost:8081/ws/' + clientId
  console.log('socketUrl', socketUrl)

  if (typeof WebSocket == 'undefined') {
    console.log('当前浏览器无法接收实时报警信息，请使用谷歌浏览器！')
    ElNotification({
      title: '提示',
      message: '当前浏览器无法接收实时报警信息，请使用谷歌浏览器！',
      type: 'warning',
      duration: 0,
    })
  } else {
    websocket.value = new WebSocket(socketUrl)
    websocket.value.onopen = () => {
      console.log('浏览器WebSocket已打开')
    }
    websocket.value.onmessage = (msg) => {
      console.log('接收到的消息', msg)
      audio1.value && audio1.value.click()
      // 重置音频，从头开始播放
      audio1.value!.currentTime = 0
      audio2.value!.currentTime = 0
      // 解析服务器通过WebSocket发送的消息
      const jsonMsg = JSON.parse(msg.data)
      if (jsonMsg.type === 1) {
        audio1.value!.play()
      } else if (jsonMsg.type === 2) {
        audio2.value!.play()
      }
      // 右上角弹窗提示
      ElNotification({
        title: jsonMsg.type === 1 ? '待接单' : '催单',
        message: jsonMsg.type === 1
          ? `<span>您有1个<span style="color:#419EFF">订单待处理</span>,${jsonMsg.content},请及时接单</span>`
          : `${jsonMsg.content}<span style='color:#419EFF;cursor: pointer'>去处理</span>`,
        duration: 0,
        dangerouslyUseHTMLString: true,
        onClick: () => {
          router.push(`/order?orderId=${jsonMsg.orderId}`).catch((err) => {
            console.log(err)
          })
          setTimeout(() => {
            location.reload()
          }, 100)
        },
      })
    }
    websocket.value.onerror = () => {
      ElNotification({
        title: '错误',
        message: '服务器错误，无法接收实时报警信息',
        type: 'error',
        duration: 0,
      })
    }
    websocket.value.onclose = () => {
      console.log('WebSocket已关闭')
    }
  }
}

const handleClose = () => {
  shopShow.value = false
}

// lifecycle hooks
onMounted(() => {
  document.addEventListener('click', handleClose)
  // getStatus()
  webSocket()
})

onBeforeUnmount(() => {
  if (websocket.value) {
    websocket.value.close()
  }
})
</script>

<template>
  <div class="common-layout">
    <el-dialog v-model="dialogStatusVisible" title="店铺状态设置" width="650px">
      <el-radio-group v-model="status_active">
        <el-radio :value="1" size="large">营业中
          <span>当前餐厅处于营业状态，自动接收任何订单，可点击打烊进入店铺打烊状态。</span>
        </el-radio>
        <el-radio :value="0" size="large">打烊中
          <span>当前餐厅处于打烊状态，仅接受营业时间内的预定订单，可点击营业中手动恢复营业状态。</span>
        </el-radio>
      </el-radio-group>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelStatus">取消</el-button>
          <el-button type="primary" @click="fixStatus">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="dialogFormVisible" title="修改密码" width="500">
      <el-form :model="form" :rules="rules" ref="pwdRef">
        <el-form-item prop="oldPwd" label="原密码" :label-width="formLabelWidth">
          <el-input v-model="form.oldPwd" autocomplete="off" />
        </el-form-item>
        <el-form-item prop="newPwd" label="新密码" :label-width="formLabelWidth">
          <el-input v-model="form.newPwd" autocomplete="off" />
        </el-form-item>
        <el-form-item prop="rePwd" label="确认密码" :label-width="formLabelWidth">
          <el-input v-model="form.rePwd" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelForm">取消</el-button>
          <el-button type="primary" @click="fixPwd">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-container>
      <el-header>
        <div style="display: inline-block; font-size: 20px; font-weight: bold; margin: 0 20px; vertical-align: top; letter-spacing: 1px;">
          简单的外卖管理系统
        </div>
        <el-icon class="icon1" v-if="isCollapse">
          <Expand @click.stop="isCollapse = !isCollapse" />
        </el-icon>
        <el-icon class="icon1" v-else>
          <Fold @click.stop="isCollapse = !isCollapse" />
        </el-icon>
        <div class="status">{{ status == 1 ? '营业中' : "打烊中" }}</div>
        <div class="rightAudio">
          <audio ref="audio1" hidden>
            <source src="../../assets/preview.mp3" type="audio/mp3" />
          </audio>
          <audio ref="audio2" hidden>
            <source src="../../assets/reminder.mp3" type="audio/mp3" />
          </audio>
        </div>
        <el-dropdown style="float: right">
          <el-button type="primary">
            {{ userInfoStore.userInfo ? userInfoStore.userInfo.account : '未登录' }}
            <el-icon class="arrow-down-icon"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="dialogFormVisible = true">修改密码</el-dropdown-item>
              <el-dropdown-item @click="quitFn">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button class="status-change" @click="dialogStatusVisible = true">店铺状态设置</el-button>
      </el-header>
      <el-container class="box1">
        <!-- 左侧导航菜单区域 -->
        <el-menu :width="isCollapse ? '640px' : '200px'" :default-active="getActiveAside()" :collapse="isCollapse"
          background-color="#22aaee" text-color="#fff" unique-opened router>
          <!-- 加了router模式，就会在激活导航时以 :index 作为path进行路径跳转（nb!不用自己写路由了!） -->
          <!-- 根据不同情况选择menu-item/submenu进行遍历，所以外层套template遍历，里面组件做判断看是否该次遍历到自己 -->
          <template v-for="item in menuList" :key="item.path">
            <el-menu-item :index="item.path">
              <el-icon>
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>

        <el-container class="mycontainer">
          <el-main>
            <router-view></router-view>
          </el-main>
          <el-footer>© 2026.6.3 take-out Tech and Fun. All rights reserved.</el-footer>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>


<style lang="less" scoped>
.common-layout {
  height: 100%;
  background:
    radial-gradient(circle at top right, rgba(64,158,255,.12), transparent 30%),
    #f5f7fa;
}

.system-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0 24px;
  color: #fff;
}

.el-header {
  height: 64px;
  background: linear-gradient(90deg,#1677ff,#409eff);
  color: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(22,119,255,.15);
}

.rightAudio {
  margin-left: auto;
}

.status {
  display: flex;
  align-items: center;
  height: 32px;
  padding: 0 16px;
  border-radius: 30px;
  background: rgba(255,255,255,.15);
  color: #fff;
  font-weight: 600;
  margin-left: 30px;
}

.status-change {
  border-radius: 20px;
  background: rgba(255,255,255,.15);
  border: 1px solid rgba(255,255,255,.2);
  color: #fff;
}

.el-dropdown .el-button {
  border-radius: 20px;
  background: rgba(255,255,255,.18);
  border: 1px solid rgba(255,255,255,.25);
  color: #fff;
}

.box1 {
  display: flex;
  height: calc(100vh - 64px);
}

.mycontainer {
  display: flex;
  flex: 1;
  flex-direction: column;
}

.el-main {
  flex: 1;
  padding: 20px;
  background: linear-gradient(180deg,#f5f7fa,#eef3f8);
}

.el-footer {
  height: 50px;
  background: #fff;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>





<style lang="less">
.el-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,.15);
}

.el-menu {
  padding-top: 20px;
  background: #001529;
  border-right: none;
}

.el-menu-item {
  margin: 8px 12px;
  border-radius: 12px;
  transition: all .3s;
}

.el-menu-item:hover {
  background: rgba(255,255,255,.08);
}

.el-menu-item.is-active {
  background: linear-gradient(
    135deg,
    #1677ff,
    #69b1ff
  ) !important;

  color: #fff !important;

  font-weight: 700;

  box-shadow:
    0 8px 20px rgba(22,119,255,.35);

  transform: translateX(3px);
}

.el-menu-item.is-active span,
.el-menu-item.is-active .el-icon,
.el-menu-item.is-active i {
  color: #fff !important;
}

.el-menu--collapse {
  width: 80px;
}


/* ===========================
   店铺状态设置弹窗优化
=========================== */

.el-radio-group {
  width: 100%;
}

.el-radio {
  width: 100%;
  margin: 0 0 16px 0 !important;

  padding: 18px 20px;

  border: 1px solid #dcdfe6;
  border-radius: 12px;

  background: #fafcff;

  transition: all .3s ease;

  display: flex !important;
  align-items: flex-start;

  box-sizing: border-box;
}

.el-radio:hover {
  border-color: #409eff;

  box-shadow:
    0 4px 12px rgba(64,158,255,.12);
}

.el-radio.is-checked {
  background: #ecf5ff;
  border-color: #409eff;
}

.el-radio__label {
  flex: 1;

  display: flex;
  flex-direction: column;

  white-space: normal !important;

  line-height: 24px;

  font-size: 16px;
  font-weight: 600;

  color: #303133;
}

.el-dialog__body {
  overflow: visible;
}

.el-radio__label span {
  display: block;

  margin-top: 10px;

  color: #606266;

  font-size: 14px;

  font-weight: normal;

  line-height: 24px;

  white-space: normal;

  word-break: break-word;
}

</style>

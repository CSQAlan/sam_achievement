<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template v-slot:header>
            <div class="clearfix">
              <span>个人信息</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon-class="user" />用户名称
                <div class="pull-right">{{ state.user.userName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="phone" />手机号码
                <div class="pull-right">{{ state.user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="email" />用户邮箱
                <div class="pull-right">{{ state.user.email }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="tree" />所属部门
                <div class="pull-right" v-if="state.user.dept">{{ state.user.dept.deptName }} / {{ state.postGroup }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="peoples" />所属角色
                <div class="pull-right">{{ state.roleGroup }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="date" />创建日期
                <div class="pull-right">{{ state.user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template v-slot:header>
            <div class="clearfix">
              <span>基本资料</span>
            </div>
          </template>
          <el-tabs v-model="selectedTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="state.user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Profile">
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import { getUserProfile } from "@/api/system/user"
import { ElMessageBox } from 'element-plus'
import { ElMessage } from 'element-plus'
import useUserStore from "@/store/modules/user"


const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const selectedTab = ref("userinfo")
const state = reactive({
  user: {},
  roleGroup: {},
  postGroup: {}
})
const roleKeys = computed(() =>
    (userStore.roles || []).map(role => String(role).replace(/^ROLE_/, "").toLowerCase())
)
const isStudent = computed(() => roleKeys.value.includes("student"))
const isTeacher = computed(() => roleKeys.value.includes("teacher"))
const isAdminPost = computed(() => String(state.postGroup || "").includes("管理员"))
const quickEntries = computed(() => {
  const entries = [
    {
      key: "participated",
      label: "我参与的成果",
      path: "/achievement/manage/participated",
      keywords: ["participated"]
    }
  ]
  if (isAdminPost.value || isTeacher.value)
  {
    entries.push({
      key: "guided",
      label: "我指导的成果",
      path: "/achievement/manage/guided",
      keywords: ["guided"]
    })
  }
  if (isAdminPost.value || isStudent.value)
  {
    entries.push({
      key: "responsible",
      label: "我负责的成果",
      path: "/achievement/manage/responsible",
      keywords: ["responsible"]
    })
  }
  if (isAdminPost.value)
  {
    entries.push({
      key: "collegeReview",
      label: "院级审核",
      path: "/achievement/college_level_unreviewed",
      keywords: ["college_level_unreviewed"]
    })
    entries.push({
      key: "schoolReview",
      label: "校级审核",
      path: "/achievement/school_level_unreviewed",
      keywords: ["school_level_unreviewed"]
    })
  }
  return entries
})

function getUser() {
  getUserProfile().then(response => {
    state.user = response.data
    state.roleGroup = response.roleGroup
    state.postGroup = response.postGroup

    // 以后端字段为准，避免 localStorage 与账号状态不一致
    const profileInitialized = Number(state.user.profileInitialized || 0) === 1
    const reminderSeen = sessionStorage.getItem(`profileReminderSeen_${state.user.userId}`) === "1"
    if (!profileInitialized && !reminderSeen) {
      showFirstVisitReminder()
    }
  })
}

// 添加提醒函数
function showFirstVisitReminder() {
  ElMessageBox.alert(
      `<div style="text-align: center; padding: 20px 10px;">
      <div style="font-size: 48px; color: #409eff; margin-bottom: 15px;">🎉</div>
      <h3 style="color: #303133; margin: 0 0 10px 0; font-size: 18px;">欢迎来到系统！</h3>
      <p style="color: #606266; margin: 0 0 15px 0; line-height: 1.5;">
        这是您首次使用本系统，为了获得更好的使用体验，<br>
        建议您先完善个人信息，包括头像、联系方式等。
      </p>
      <div style="background: #f5f7fa; padding: 12px; border-radius: 6px; border-left: 4px solid #409eff;">
        <strong style="color: #409eff;">💡 小贴士：</strong><br>
        完善信息后，您可以享受更多个性化服务！
      </div>
    </div>`,
      '首次使用指南',
      {
        type: 'info',
        confirmButtonText: '立即完善',
        confirmButtonClass: 'el-button--primary',
        customClass: 'welcome-dialog',
        dangerouslyUseHTMLString: true,
        showClose: false
      }
  ).then(() => {
    sessionStorage.setItem(`profileReminderSeen_${state.user.userId}`, "1")
  })
}

function resolveRoutePath(entry) {
  const routes = router.getRoutes()
  const found = routes.find(route => {
    const routePath = String(route.path || "").toLowerCase()
    const routeName = String(route.name || "").toLowerCase()
    return (entry.keywords || []).some(keyword =>
        routePath.includes(keyword) || routeName.includes(keyword)
    )
  })
  return found?.path || entry.path
}

function goQuick(entry) {
  const path = resolveRoutePath(entry)
  router.push(path).catch(() => {
    ElMessage.warning("当前页面路由未配置，请联系管理员补充菜单")
  })
}

onMounted(() => {
  const activeTab = route.params && route.params.activeTab
  if (activeTab) {
    selectedTab.value = activeTab
  }
  getUser()
})
</script>

<style scoped>
.welcome-dialog {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-dialog .el-message-box__header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

.welcome-dialog .el-message-box__title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.welcome-dialog .el-message-box__content {
  padding: 10px 20px;
}

.welcome-dialog .el-message-box__message {
  padding: 0;
}

.welcome-dialog .el-message-box__btns {
  padding: 10px 20px 20px;
}

.welcome-dialog .el-button {
  border-radius: 6px;
  font-weight: 500;
  padding: 10px 24px;
}

.quick-access {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-tag {
  cursor: pointer;
  font-size: 14px;
  padding: 8px 12px;
}
</style>



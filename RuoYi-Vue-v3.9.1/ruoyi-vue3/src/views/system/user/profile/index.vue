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
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd />
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
import resetPwd from "./resetPwd"
import { getUserProfile } from "@/api/system/user"
import { ElMessageBox } from 'element-plus'


const route = useRoute()
const selectedTab = ref("userinfo")
const state = reactive({
  user: {},
  roleGroup: {},
  postGroup: {}
})

function getUser() {
  getUserProfile().then(response => {
    state.user = response.data
    state.roleGroup = response.roleGroup
    state.postGroup = response.postGroup

    // 检查是否是首次访问，如果是则显示提醒弹框
    const isFirstVisit = localStorage.getItem(`isFirstVisit_${state.user.userId}`) !== 'false'
    if (isFirstVisit) {
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
    // 标记为已访问
    localStorage.setItem(`isFirstVisit_${state.user.userId}`, 'false')
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
</style>

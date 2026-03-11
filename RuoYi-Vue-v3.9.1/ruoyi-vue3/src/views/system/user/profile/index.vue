<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>&#20010;&#20154;&#20449;&#24687;</span>
            </div>
          </template>
          <div>
            <div class="text-center">
              <userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon-class="user" />&#29992;&#25143;&#21517;&#31216;
                <div class="pull-right">{{ state.user.userName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="phone" />&#25163;&#26426;&#21495;&#30721;
                <div class="pull-right">{{ state.user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="email" />&#29992;&#25143;&#37038;&#31665;
                <div class="pull-right">{{ state.user.email }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="tree" />&#25152;&#23646;&#37096;&#38376;
                <div v-if="state.user.dept" class="pull-right">{{ state.user.dept.deptName }} / {{ state.postGroup }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="peoples" />&#25152;&#23646;&#35282;&#33394;
                <div class="pull-right">{{ state.roleGroup }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="date" />&#21019;&#24314;&#26085;&#26399;
                <div class="pull-right">{{ state.user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <div class="clearfix">
              <span>&#22522;&#26412;&#36164;&#26009;</span>
            </div>
          </template>
          <el-tabs v-model="selectedTab">
            <el-tab-pane label="&#22522;&#26412;&#36164;&#26009;" name="userinfo">
              <userInfo :user="state.user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Profile">
import { ElMessageBox } from 'element-plus'
import { getUserProfile } from '@/api/system/user'
import useUserStore from '@/store/modules/user'
import userAvatar from './userAvatar'
import userInfo from './userInfo'

const route = useRoute()
const userStore = useUserStore()
const selectedTab = ref('userinfo')
const completionDialogOpened = ref(false)
const state = reactive({
  user: {},
  roleGroup: {},
  postGroup: {}
})

function getUser() {
  getUserProfile().then(response => {
    state.user = response.data || {}
    state.roleGroup = response.roleGroup || {}
    state.postGroup = response.postGroup || {}

    const profileInitialized = Number(state.user.profileInitialized || 0) === 1
    userStore.profileInitialized = profileInitialized ? 1 : 0
    if (!profileInitialized) {
      showProfileRequiredDialog()
    }
  })
}

function showProfileRequiredDialog() {
  if (completionDialogOpened.value) {
    return
  }

  completionDialogOpened.value = true
  ElMessageBox.alert(
    `<div style="line-height: 1.8; padding: 8px 0;">
      <p style="margin: 0 0 12px;">\u5f53\u524d\u8d26\u53f7\u7684\u4e2a\u4eba\u4e2d\u5fc3\u4fe1\u606f\u5c1a\u672a\u5b8c\u5584\u3002</p>
      <p style="margin: 0;">\u8bf7\u5148\u8865\u5168\u4e2a\u4eba\u8d44\u6599\u540e\uff0c\u518d\u7ee7\u7eed\u4f7f\u7528\u7cfb\u7edf\u5176\u4ed6\u529f\u80fd\u3002</p>
    </div>`,
    '\u8bf7\u5148\u5b8c\u5584\u4e2a\u4eba\u4e2d\u5fc3',
    {
      type: 'warning',
      confirmButtonText: '\u53bb\u5b8c\u5584',
      confirmButtonClass: 'el-button--primary',
      customClass: 'profile-required-dialog',
      dangerouslyUseHTMLString: true,
      showClose: false,
      closeOnClickModal: false,
      closeOnPressEscape: false,
      closeOnHashChange: false
    }
  ).catch(() => {})
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
.profile-required-dialog {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.profile-required-dialog .el-message-box__header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

.profile-required-dialog .el-message-box__title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.profile-required-dialog .el-message-box__content {
  padding: 10px 20px;
}

.profile-required-dialog .el-message-box__message {
  padding: 0;
}

.profile-required-dialog .el-message-box__btns {
  padding: 10px 20px 20px;
}

.profile-required-dialog .el-button {
  border-radius: 6px;
  font-weight: 500;
  padding: 10px 24px;
}
</style>
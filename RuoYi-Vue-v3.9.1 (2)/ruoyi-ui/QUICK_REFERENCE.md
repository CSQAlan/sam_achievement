# Vue2 → Vue3 快速参考

## 常用替换对照表

### Store 使用

| Vue2 (Vuex) | Vue3 (Pinia) |
|-------------|--------------|
| `this.$store.state.user.token` | `useUserStore().token` |
| `this.$store.getters.roles` | `useUserStore().roles` |
| `this.$store.commit('SET_TOKEN', token)` | `useUserStore().token = token` |
| `this.$store.dispatch('Login', data)` | `useUserStore().login(data)` |
| `this.$store.dispatch('LogOut')` | `useUserStore().logOut()` |
| `this.$store.dispatch('GetInfo')` | `useUserStore().getInfo()` |

### Element UI → Element Plus

| Vue2 | Vue3 |
|------|------|
| `import { Message } from 'element-ui'` | `import { ElMessage } from 'element-plus'` |
| `import { MessageBox } from 'element-ui'` | `import { ElMessageBox } from 'element-plus'` |
| `Message.success()` | `ElMessage.success()` |
| `MessageBox.confirm()` | `ElMessageBox.confirm()` |
| `this.$message.success()` | `ElMessage.success()` |
| `this.$confirm()` | `ElMessageBox.confirm()` |

### 生命周期钩子

| Vue2 | Vue3 |
|------|------|
| `beforeDestroy()` | `beforeUnmount()` |
| `destroyed()` | `unmounted()` |

### 环境变量

| Vue2 | Vue3 |
|------|------|
| `process.env.VUE_APP_BASE_API` | `import.meta.env.VITE_APP_BASE_API` |
| `process.env.NODE_ENV` | `import.meta.env.MODE` |

### 路由

| Vue2 | Vue3 |
|------|------|
| `router.addRoutes(routes)` | `routes.forEach(route => router.addRoute(route))` |
| `path: '*'` | `path: '/:pathMatch(.*)*'` |

### 组件

| Vue2 | Vue3 |
|------|------|
| `@click.native` | `@click` |
| `v-bind="$attrs" v-on="$listeners"` | `v-bind="$attrs"` |
| `props: ['value']` | `props: ['modelValue']` |
| `$emit('input', val)` | `$emit('update:modelValue', val)` |

## 组件模板示例

### 选项式 API (兼容方式)

```vue
<template>
  <div>
    <el-button @click="handleLogin">登录</el-button>
    <div>{{ userInfo.name }}</div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

export default {
  name: 'MyComponent',
  data() {
    return {
      loginForm: {}
    }
  },
  computed: {
    userInfo() {
      return useUserStore()
    },
    token() {
      return useUserStore().token
    }
  },
  methods: {
    async handleLogin() {
      try {
        await useUserStore().login(this.loginForm)
        ElMessage.success('登录成功')
      } catch (error) {
        ElMessage.error('登录失败')
      }
    }
  },
  beforeUnmount() {
    // 清理工作
  }
}
</script>
```

### 组合式 API (推荐方式)

```vue
<template>
  <div>
    <el-button @click="handleLogin">登录</el-button>
    <div>{{ userStore.name }}</div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const loginForm = ref({})

const token = computed(() => userStore.token)

const handleLogin = async () => {
  try {
    await userStore.login(loginForm.value)
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error('登录失败')
  }
}

// 生命周期
onBeforeUnmount(() => {
  // 清理工作
})
</script>
```

## Store 导入参考

```javascript
// 用户相关
import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()
// userStore.token, userStore.roles, userStore.permissions
// userStore.login(), userStore.logOut(), userStore.getInfo()

// 应用配置
import { useAppStore } from '@/store/modules/app'
const appStore = useAppStore()
// appStore.sidebar, appStore.device, appStore.size
// appStore.toggleSideBar(), appStore.setSize()

// 权限路由
import { usePermissionStore } from '@/store/modules/permission'
const permissionStore = usePermissionStore()
// permissionStore.routes, permissionStore.sidebarRouters
// permissionStore.generateRoutes()

// 系统设置
import { useSettingsStore } from '@/store/modules/settings'
const settingsStore = useSettingsStore()
// settingsStore.theme, settingsStore.sideTheme
// settingsStore.changeSetting(), settingsStore.setTitle()

// 标签视图
import { useTagsViewStore } from '@/store/modules/tagsView'
const tagsViewStore = useTagsViewStore()
// tagsViewStore.visitedViews, tagsViewStore.cachedViews
// tagsViewStore.addView(), tagsViewStore.delView()

// 字典
import { useDictStore } from '@/store/modules/dict'
const dictStore = useDictStore()
// dictStore.dict
// dictStore.setDict(), dictStore.removeDict()
```

## 常见错误和解决方案

### 错误 1: Cannot read property 'xxx' of undefined
```javascript
// 错误
this.$store.state.user.token

// 正确
import { useUserStore } from '@/store/modules/user'
useUserStore().token
```

### 错误 2: Message is not defined
```javascript
// 错误
Message.success('成功')

// 正确
import { ElMessage } from 'element-plus'
ElMessage.success('成功')
```

### 错误 3: process.env.VUE_APP_BASE_API is undefined
```javascript
// 错误
const baseURL = process.env.VUE_APP_BASE_API

// 正确
const baseURL = import.meta.env.VITE_APP_BASE_API
```

### 错误 4: beforeDestroy is not a valid lifecycle hook
```javascript
// 错误
beforeDestroy() { }

// 正确
beforeUnmount() { }
```

### 错误 5: addRoutes is not a function
```javascript
// 错误
router.addRoutes(routes)

// 正确
routes.forEach(route => router.addRoute(route))
```

## 批量查找替换命令

在 VS Code 中使用正则查找替换：

1. **环境变量**
   - 查找: `process\.env\.VUE_APP_`
   - 替换: `import.meta.env.VITE_APP_`

2. **Element UI 导入**
   - 查找: `from ['"]element-ui['"]`
   - 替换: `from 'element-plus'`

3. **Message 使用**
   - 查找: `\bMessage\.`
   - 替换: `ElMessage.`

4. **生命周期**
   - 查找: `\bbeforeDestroy\b`
   - 替换: `beforeUnmount`
   - 查找: `\bdestroyed\b`
   - 替换: `unmounted`

## 测试检查清单

- [ ] 登录页面正常
- [ ] 路由跳转正常
- [ ] 菜单显示正常
- [ ] 用户信息显示正常
- [ ] 权限控制正常
- [ ] 字典数据加载正常
- [ ] 表格分页正常
- [ ] 表单验证正常
- [ ] 文件上传正常
- [ ] 图片预览正常
- [ ] 富文本编辑器正常
- [ ] 消息提示正常
- [ ] 确认对话框正常

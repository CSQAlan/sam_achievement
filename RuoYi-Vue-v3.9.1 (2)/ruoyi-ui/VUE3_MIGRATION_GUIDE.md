# Vue2 到 Vue3 迁移指南

## 已完成的主要更改

### 1. 依赖更新
- Vue 2.6 → Vue 3.4
- Vue Router 3.x → Vue Router 4.x
- Vuex → Pinia
- Element UI → Element Plus
- Webpack (Vue CLI) → Vite

### 2. 构建工具
- 从 Vue CLI 迁移到 Vite
- 创建了新的 `vite.config.js`
- 更新了环境变量前缀：`VUE_APP_` → `VITE_APP_`

### 3. Store 迁移 (Vuex → Pinia)
所有 store 模块已转换为 Pinia stores：
- `useUserStore` - 用户信息
- `useAppStore` - 应用配置
- `usePermissionStore` - 权限路由
- `useSettingsStore` - 系统设置
- `useTagsViewStore` - 标签视图
- `useDictStore` - 字典数据

#### 使用方式变更：
```javascript
// Vue2 (Vuex)
this.$store.state.user.token
this.$store.commit('SET_TOKEN', token)
this.$store.dispatch('Login', userInfo)

// Vue3 (Pinia)
import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()
userStore.token
userStore.token = newToken  // 直接修改
userStore.login(userInfo)   // 调用 action
```

### 4. 需要手动更新的文件

#### 4.1 所有 .vue 组件文件
需要更新以下内容：

1. **组件选项式 API 保持兼容**，但建议逐步迁移到组合式 API
2. **生命周期钩子**：
   - `beforeDestroy` → `beforeUnmount`
   - `destroyed` → `unmounted`

3. **事件修饰符**：
   - `.native` 修饰符已移除
   - `$listeners` 已移除

4. **v-model 变更**：
   ```vue
   <!-- Vue2 -->
   <child-component v-model="value" />
   <!-- 等同于 -->
   <child-component :value="value" @input="value = $event" />
   
   <!-- Vue3 -->
   <child-component v-model="value" />
   <!-- 等同于 -->
   <child-component :modelValue="value" @update:modelValue="value = $event" />
   ```

5. **过滤器已移除**：
   ```vue
   <!-- Vue2 -->
   {{ message | capitalize }}
   
   <!-- Vue3 - 使用方法或计算属性 -->
   {{ capitalize(message) }}
   ```

6. **$attrs 包含 class 和 style**

7. **Store 使用**：
   ```vue
   <script>
   // Vue2
   export default {
     computed: {
       token() {
         return this.$store.state.user.token
       }
     },
     methods: {
       async login() {
         await this.$store.dispatch('Login', this.loginForm)
       }
     }
   }
   </script>
   
   <script setup>
   // Vue3 组合式 API (推荐)
   import { useUserStore } from '@/store/modules/user'
   const userStore = useUserStore()
   const token = computed(() => userStore.token)
   
   const login = async () => {
     await userStore.login(loginForm.value)
   }
   </script>
   
   <script>
   // Vue3 选项式 API (兼容)
   import { mapStores } from 'pinia'
   import { useUserStore } from '@/store/modules/user'
   
   export default {
     computed: {
       ...mapStores(useUserStore),
       token() {
         return this.userStore.token
       }
     },
     methods: {
       async login() {
         await this.userStore.login(this.loginForm)
       }
     }
   }
   </script>
   ```

#### 4.2 Router 配置
需要更新 `router/index.js`：
```javascript
// Vue2
import VueRouter from 'vue-router'
Vue.use(VueRouter)
const router = new VueRouter({ routes })
router.addRoutes(routes)  // 已废弃

// Vue3
import { createRouter, createWebHistory } from 'vue-router'
const router = createRouter({
  history: createWebHistory(),
  routes
})
router.addRoute(route)  // 新 API
```

#### 4.3 Element UI → Element Plus
组件名称和 API 有变化：
```javascript
// Vue2
import { Message, MessageBox } from 'element-ui'
Message.success('成功')
MessageBox.confirm('确认?')

// Vue3
import { ElMessage, ElMessageBox } from 'element-plus'
ElMessage.success('成功')
ElMessageBox.confirm('确认?')
```

#### 4.4 全局 API
```javascript
// Vue2
Vue.prototype.$message = Message
this.$message.success('成功')

// Vue3
app.config.globalProperties.$message = ElMessage
// 或使用组合式 API
import { ElMessage } from 'element-plus'
ElMessage.success('成功')
```

### 5. 环境变量
所有环境变量已更新：
- `.env.development`: `VUE_APP_*` → `VITE_APP_*`
- `.env.production`: `VUE_APP_*` → `VITE_APP_*`

代码中使用：
```javascript
// Vue2
process.env.VUE_APP_BASE_API

// Vue3
import.meta.env.VITE_APP_BASE_API
```

### 6. 安装和运行

```bash
# 删除旧依赖
rm -rf node_modules package-lock.json

# 安装新依赖
npm install

# 开发运行
npm run dev

# 生产构建
npm run build:prod
```

## 需要批量更新的内容

### 批量替换建议：

1. **Store 调用**：
   - 搜索：`this.$store.state.user`
   - 替换为：使用 `useUserStore()`

2. **环境变量**：
   - 搜索：`process.env.VUE_APP_`
   - 替换为：`import.meta.env.VITE_APP_`

3. **Element UI**：
   - 搜索：`element-ui`
   - 替换为：`element-plus`
   - 搜索：`Message.`
   - 替换为：`ElMessage.`

4. **生命周期**：
   - 搜索：`beforeDestroy`
   - 替换为：`beforeUnmount`
   - 搜索：`destroyed`
   - 替换为：`unmounted`

5. **路由**：
   - 搜索：`addRoutes`
   - 替换为：`addRoute`

## 注意事项

1. Vue3 移除了 `$on`、`$off`、`$once` 等事件 API
2. 过滤器 (filters) 已完全移除
3. `$children` 已移除
4. 函数式组件的创建方式改变
5. 异步组件需要使用 `defineAsyncComponent`
6. Teleport 替代了 Portal
7. 多个 v-model 支持

## 推荐迁移顺序

1. ✅ 更新依赖和构建配置
2. ✅ 迁移 Store (Vuex → Pinia)
3. ✅ 更新 main.js 入口文件
4. ⏳ 更新 router 配置
5. ⏳ 更新所有组件中的 store 调用
6. ⏳ 更新所有组件中的 Element UI 引用
7. ⏳ 更新环境变量引用
8. ⏳ 测试所有功能
9. ⏳ 逐步迁移到组合式 API (可选)

## 参考文档

- [Vue 3 迁移指南](https://v3-migration.vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [Vite 文档](https://vitejs.dev/)
- [Vue Router 4 文档](https://router.vuejs.org/)

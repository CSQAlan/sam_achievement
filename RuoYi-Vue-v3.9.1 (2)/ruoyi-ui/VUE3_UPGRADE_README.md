# RuoYi Vue3 升级说明

## 已完成的工作

### 1. 核心配置文件 ✅
- ✅ `package.json` - 更新所有依赖到 Vue3 版本
- ✅ `vite.config.js` - 创建 Vite 配置文件
- ✅ `index.html` - 移动到根目录并更新
- ✅ `.env.development` - 更新环境变量前缀
- ✅ `.env.production` - 更新环境变量前缀

### 2. 入口文件 ✅
- ✅ `src/main.js` - 完全重写为 Vue3 语法

### 3. Store (Vuex → Pinia) ✅
- ✅ `src/store/index.js` - 改用 Pinia
- ✅ `src/store/modules/user.js` - 转换为 Pinia store
- ✅ `src/store/modules/app.js` - 转换为 Pinia store
- ✅ `src/store/modules/permission.js` - 转换为 Pinia store
- ✅ `src/store/modules/settings.js` - 转换为 Pinia store
- ✅ `src/store/modules/tagsView.js` - 转换为 Pinia store
- ✅ `src/store/modules/dict.js` - 转换为 Pinia store

### 4. 路由 ✅
- ✅ `src/router/index.js` - 更新为 Vue Router 4
- ✅ `src/permission.js` - 更新路由守卫

### 5. 辅助文件 ✅
- ✅ `src/components/SvgIcon/svgicon.js` - Element Plus 图标注册

## 需要完成的工作

### 1. 安装依赖
```bash
cd ruoyi-ui

# 删除旧的依赖
rm -rf node_modules package-lock.json

# 安装新依赖
npm install
```

### 2. 运行自动迁移脚本（可选）
这个脚本会自动替换一些常见的 Vue2 语法：

```bash
node migration-script.js
```

脚本会自动处理：
- 环境变量引用 (`process.env.VUE_APP_` → `import.meta.env.VITE_APP_`)
- Element UI 导入和使用
- 生命周期钩子名称
- 部分 Store 调用

### 3. 手动更新组件

由于项目较大，以下内容需要手动检查和更新：

#### 3.1 所有 .vue 组件文件

需要检查和更新的内容：

**A. Store 使用方式**

在组件的 `<script>` 部分添加 store 导入：

```vue
<script>
// 添加导入
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import { useSettingsStore } from '@/store/modules/settings'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { useDictStore } from '@/store/modules/dict'

export default {
  setup() {
    // 在 setup 中使用
    const userStore = useUserStore()
    return { userStore }
  },
  computed: {
    // 或在 computed 中使用
    token() {
      return useUserStore().token
    }
  },
  methods: {
    async handleLogin() {
      // 在方法中使用
      await useUserStore().login(this.loginForm)
    }
  }
}
</script>
```

**B. Element UI → Element Plus**

```javascript
// 查找并替换
import { Message } from 'element-ui'
// 改为
import { ElMessage } from 'element-plus'

// 使用时
Message.success('成功')
// 改为
ElMessage.success('成功')
```

**C. 生命周期钩子**

```javascript
// 查找并替换
beforeDestroy() { }
// 改为
beforeUnmount() { }

destroyed() { }
// 改为
unmounted() { }
```

**D. v-model 自定义组件**

如果组件使用了自定义 v-model：

```vue
<!-- Vue2 -->
<script>
export default {
  props: ['value'],
  methods: {
    updateValue(val) {
      this.$emit('input', val)
    }
  }
}
</script>

<!-- Vue3 -->
<script>
export default {
  props: ['modelValue'],
  emits: ['update:modelValue'],
  methods: {
    updateValue(val) {
      this.$emit('update:modelValue', val)
    }
  }
}
</script>
```

**E. 过滤器 (Filters)**

Vue3 移除了过滤器，需要改用方法或计算属性：

```vue
<!-- Vue2 -->
<template>
  <div>{{ message | capitalize }}</div>
</template>

<!-- Vue3 -->
<template>
  <div>{{ capitalize(message) }}</div>
</template>

<script>
export default {
  methods: {
    capitalize(value) {
      if (!value) return ''
      return value.charAt(0).toUpperCase() + value.slice(1)
    }
  }
}
</script>
```

**F. $listeners 和 $attrs**

Vue3 中 `$listeners` 已合并到 `$attrs`：

```vue
<!-- Vue2 -->
<child-component v-bind="$attrs" v-on="$listeners" />

<!-- Vue3 -->
<child-component v-bind="$attrs" />
```

**G. .native 修饰符**

Vue3 移除了 `.native` 修饰符：

```vue
<!-- Vue2 -->
<child-component @click.native="handleClick" />

<!-- Vue3 -->
<child-component @click="handleClick" />
```

#### 3.2 需要特别注意的文件

以下文件可能需要较多修改：

1. **布局组件**
   - `src/layout/**/*.vue`
   - 检查 store 使用、Element UI 组件

2. **系统管理页面**
   - `src/views/system/**/*.vue`
   - 大量使用 store 和 Element UI

3. **监控页面**
   - `src/views/monitor/**/*.vue`

4. **工具页面**
   - `src/views/tool/**/*.vue`

5. **公共组件**
   - `src/components/**/*.vue`
   - 特别注意 DictData、Editor、FileUpload 等

#### 3.3 插件和工具类

检查以下文件：

- `src/plugins/*.js` - 可能需要更新插件注册方式
- `src/directive/*.js` - 自定义指令语法有变化
- `src/utils/*.js` - 检查是否有使用 Vue 实例的地方

### 4. 测试和调试

```bash
# 启动开发服务器
npm run dev
```

逐个测试功能模块：
1. 登录/登出
2. 菜单导航
3. 用户管理
4. 角色管理
5. 字典管理
6. 其他业务功能

### 5. 常见问题和解决方案

#### 问题 1: 找不到模块
```
Error: Cannot find module 'element-ui'
```
**解决**: 确保已运行 `npm install` 安装新依赖

#### 问题 2: Store 未定义
```
Error: useUserStore is not defined
```
**解决**: 在组件中添加 store 导入

#### 问题 3: 路由跳转失败
```
Error: addRoutes is not a function
```
**解决**: 已在 router/index.js 中更新，确保使用新的路由配置

#### 问题 4: Element Plus 样式问题
**解决**: 确保在 main.js 中导入了 `element-plus/dist/index.css`

#### 问题 5: 环境变量未定义
```
Error: process.env.VUE_APP_BASE_API is undefined
```
**解决**: 使用 `import.meta.env.VITE_APP_BASE_API`

### 6. 性能优化建议

升级到 Vue3 后，可以考虑：

1. **使用组合式 API**
   - 逐步将组件改写为 `<script setup>` 语法
   - 更好的代码组织和类型推导

2. **按需导入 Element Plus**
   - 已配置自动导入插件
   - 减小打包体积

3. **使用 Vite 的特性**
   - 更快的热更新
   - 更好的开发体验

### 7. 构建和部署

```bash
# 生产构建
npm run build:prod

# 预览构建结果
npm run preview
```

## 迁移检查清单

- [ ] 安装新依赖
- [ ] 运行自动迁移脚本
- [ ] 更新所有 .vue 组件中的 store 调用
- [ ] 更新所有 Element UI 引用
- [ ] 更新所有环境变量引用
- [ ] 更新生命周期钩子
- [ ] 移除过滤器，改用方法
- [ ] 测试登录功能
- [ ] 测试路由导航
- [ ] 测试所有业务功能
- [ ] 检查控制台错误
- [ ] 生产构建测试

## 参考资源

- [Vue 3 迁移指南](https://v3-migration.vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [Vite 文档](https://vitejs.dev/)
- [Vue Router 4 文档](https://router.vuejs.org/)

## 获取帮助

如果遇到问题：
1. 查看 `VUE3_MIGRATION_GUIDE.md` 详细迁移指南
2. 检查浏览器控制台错误信息
3. 参考官方文档
4. 在项目中搜索类似的已迁移代码作为参考

## 注意事项

⚠️ **重要**: 
- 建议在迁移前创建 Git 分支
- 逐步测试，不要一次性修改所有文件
- 保留 Vue2 版本作为备份
- 充分测试后再部署到生产环境

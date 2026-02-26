# Vue3 版本安装和运行指南

## 📦 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0

检查版本：
```bash
node -v
npm -v
```

## 🚀 快速开始

### 步骤 1: 清理旧依赖

```bash
# 进入前端项目目录
cd ruoyi-ui

# 删除旧的依赖和锁文件
rm -rf node_modules
rm package-lock.json
# 如果使用 yarn
rm yarn.lock
```

### 步骤 2: 安装依赖

```bash
# 使用 npm
npm install

# 或使用 yarn
yarn install

# 或使用 pnpm（推荐，更快）
pnpm install
```

**注意**: 首次安装可能需要 5-10 分钟，请耐心等待。

### 步骤 3: 运行自动迁移脚本（可选）

这个脚本会自动更新一些常见的 Vue2 语法：

```bash
node migration-script.js
```

脚本会输出修改的文件数量和替换统计。

### 步骤 4: 启动开发服务器

```bash
# 开发模式
npm run dev

# 或
yarn dev
```

浏览器会自动打开 `http://localhost` (默认端口 80)

如果端口被占用，可以修改 `vite.config.js` 中的 `server.port` 配置。

## 🔧 可用命令

```bash
# 开发模式（热更新）
npm run dev

# 生产构建
npm run build:prod

# 预发布构建
npm run build:stage

# 预览构建结果
npm run preview
```

## 📝 配置说明

### 环境变量

项目使用不同的环境变量文件：

- `.env.development` - 开发环境
- `.env.production` - 生产环境
- `.env.staging` - 预发布环境

主要配置项：
```bash
# 应用标题
VITE_APP_TITLE = 若依管理系统

# API 基础路径
VITE_APP_BASE_API = '/dev-api'
```

### 后端接口配置

修改 `vite.config.js` 中的代理配置：

```javascript
server: {
  proxy: {
    '/dev-api': {
      target: 'http://localhost:8080',  // 修改为你的后端地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/dev-api/, '')
    }
  }
}
```

## 🐛 常见问题

### 问题 1: 安装依赖失败

**错误信息**:
```
npm ERR! code ERESOLVE
npm ERR! ERESOLVE unable to resolve dependency tree
```

**解决方案**:
```bash
# 清理 npm 缓存
npm cache clean --force

# 使用 --legacy-peer-deps 安装
npm install --legacy-peer-deps

# 或使用 --force
npm install --force
```

### 问题 2: 端口被占用

**错误信息**:
```
Port 80 is already in use
```

**解决方案**:

方法 1: 修改 `vite.config.js`
```javascript
server: {
  port: 3000,  // 改为其他端口
}
```

方法 2: 关闭占用端口的程序
```bash
# Windows
netstat -ano | findstr :80
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i :80
kill -9 <进程ID>
```

### 问题 3: 模块找不到

**错误信息**:
```
Error: Cannot find module 'xxx'
```

**解决方案**:
```bash
# 重新安装依赖
rm -rf node_modules package-lock.json
npm install
```

### 问题 4: Vite 启动慢

**解决方案**:

1. 使用 pnpm 代替 npm
```bash
npm install -g pnpm
pnpm install
pnpm dev
```

2. 配置依赖预构建（已在 vite.config.js 中配置）

### 问题 5: 热更新不生效

**解决方案**:

1. 检查文件是否在 `src` 目录下
2. 重启开发服务器
3. 清理浏览器缓存

### 问题 6: 样式不生效

**错误信息**:
```
Preprocessor dependency "sass" not found
```

**解决方案**:
```bash
npm install -D sass
```

### 问题 7: Element Plus 组件样式缺失

**解决方案**:

确保 `main.js` 中导入了样式：
```javascript
import 'element-plus/dist/index.css'
```

## 🔍 验证安装

安装完成后，检查以下内容：

### 1. 开发服务器启动成功
```
VITE v5.3.0  ready in xxx ms

➜  Local:   http://localhost:80/
➜  Network: http://192.168.x.x:80/
```

### 2. 浏览器无错误

打开浏览器控制台（F12），确保没有红色错误信息。

### 3. 登录页面正常显示

访问 `http://localhost`，应该能看到登录页面。

### 4. 基本功能测试

- [ ] 登录功能
- [ ] 菜单导航
- [ ] 页面跳转
- [ ] 数据加载

## 📊 性能优化建议

### 1. 使用 pnpm

pnpm 比 npm 更快，占用空间更小：

```bash
npm install -g pnpm
pnpm install
```

### 2. 启用 Vite 缓存

Vite 会自动缓存依赖，首次启动后会更快。

### 3. 配置 CDN（生产环境）

在 `vite.config.js` 中配置外部依赖：

```javascript
build: {
  rollupOptions: {
    external: ['vue', 'vue-router', 'pinia'],
    output: {
      globals: {
        vue: 'Vue',
        'vue-router': 'VueRouter',
        pinia: 'Pinia'
      }
    }
  }
}
```

然后在 `index.html` 中引入 CDN：

```html
<script src="https://cdn.jsdelivr.net/npm/vue@3/dist/vue.global.prod.js"></script>
```

## 🏗️ 生产构建

### 构建命令

```bash
# 生产环境构建
npm run build:prod

# 预发布环境构建
npm run build:stage
```

### 构建产物

构建完成后，产物在 `dist` 目录：

```
dist/
├── index.html
├── static/
│   ├── js/
│   ├── css/
│   └── img/
└── favicon.ico
```

### 部署

将 `dist` 目录的内容部署到 Web 服务器：

**Nginx 配置示例**:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /prod-api/ {
        proxy_pass http://localhost:8080/;
    }
}
```

## 📚 下一步

1. 阅读 `MIGRATION_SUMMARY.md` 了解迁移进度
2. 查看 `QUICK_REFERENCE.md` 学习新语法
3. 参考 `VUE3_MIGRATION_GUIDE.md` 进行组件更新
4. 开始测试和调试

## 🆘 获取帮助

如果遇到问题：

1. 查看项目文档
   - `MIGRATION_SUMMARY.md` - 迁移总结
   - `VUE3_MIGRATION_GUIDE.md` - 详细指南
   - `QUICK_REFERENCE.md` - 快速参考

2. 检查日志
   - 浏览器控制台
   - 终端输出
   - Network 面板

3. 参考官方文档
   - [Vue 3](https://vuejs.org/)
   - [Vite](https://vitejs.dev/)
   - [Element Plus](https://element-plus.org/)

4. 社区支持
   - [Vue 论坛](https://forum.vuejs.org/)
   - [GitHub Issues](https://github.com/vuejs/core/issues)

## ✅ 安装检查清单

- [ ] Node.js 版本 >= 16
- [ ] 删除旧依赖
- [ ] 安装新依赖成功
- [ ] 开发服务器启动成功
- [ ] 浏览器无错误
- [ ] 登录页面正常显示
- [ ] 可以正常登录
- [ ] 菜单导航正常
- [ ] 数据加载正常

---

**祝你使用愉快！** 🎉

/**
 * 全局配置文件
 */

// 应用配置
export const appConfig = {
  // 应用名称
  name: import.meta.env.VITE_APP_TITLE || '若依管理系统',
  
  // 版本号
  version: '3.9.1',
  
  // 是否开启开发模式
  isDev: import.meta.env.DEV,
  
  // 是否开启生产模式
  isProd: import.meta.env.PROD,
  
  // API 基础路径
  baseURL: import.meta.env.VITE_APP_BASE_API || '/dev-api',
  
  // 上传文件路径
  uploadURL: import.meta.env.VITE_APP_BASE_API + '/common/upload',
  
  // 图片预览路径前缀
  imagePreviewPrefix: import.meta.env.VITE_APP_BASE_API
}

// 表格配置
export const tableConfig = {
  // 默认每页显示条数
  defaultPageSize: 10,
  
  // 每页显示条数选项
  pageSizes: [10, 20, 30, 50, 100],
  
  // 分页布局
  paginationLayout: 'total, sizes, prev, pager, next, jumper',
  
  // 是否显示边框
  border: false,
  
  // 是否显示斑马纹
  stripe: false,
  
  // 表格大小
  size: 'default',
  
  // 是否高亮当前行
  highlightCurrentRow: true
}

// 表单配置
export const formConfig = {
  // 标签宽度
  labelWidth: '100px',
  
  // 标签位置
  labelPosition: 'right',
  
  // 表单大小
  size: 'default',
  
  // 是否显示必填星号
  requireAsteriskPosition: 'left',
  
  // 是否在输入框中显示校验结果反馈图标
  statusIcon: false,
  
  // 是否在 rules 属性改变后立即触发一次验证
  validateOnRuleChange: true
}

// 对话框配置
export const dialogConfig = {
  // 默认宽度
  width: '600px',
  
  // 是否可以通过点击 modal 关闭
  closeOnClickModal: false,
  
  // 是否可以通过按下 ESC 关闭
  closeOnPressEscape: true,
  
  // 是否显示关闭按钮
  showClose: true,
  
  // 是否需要遮罩层
  modal: true,
  
  // 是否在 Dialog 出现时将 body 滚动锁定
  lockScroll: true,
  
  // 是否将 Dialog 自身插入至 body 元素上
  appendToBody: true,
  
  // 是否对头部和底部采用居中布局
  center: false,
  
  // 是否可以拖拽
  draggable: false
}

// 上传配置
export const uploadConfig = {
  // 最大上传文件大小（MB）
  maxSize: 10,
  
  // 允许上传的图片类型
  imageTypes: ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'],
  
  // 允许上传的文档类型
  documentTypes: ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt'],
  
  // 允许上传的视频类型
  videoTypes: ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv'],
  
  // 允许上传的音频类型
  audioTypes: ['mp3', 'wav', 'ogg', 'aac', 'flac'],
  
  // 允许上传的压缩包类型
  archiveTypes: ['zip', 'rar', '7z', 'tar', 'gz'],
  
  // 上传请求头
  headers: {
    'Content-Type': 'multipart/form-data'
  },
  
  // 是否支持多选
  multiple: false,
  
  // 是否显示已上传文件列表
  showFileList: true,
  
  // 是否启用拖拽上传
  drag: false,
  
  // 接受上传的文件类型
  accept: '*'
}

// 日期配置
export const dateConfig = {
  // 日期格式
  dateFormat: 'YYYY-MM-DD',
  
  // 日期时间格式
  datetimeFormat: 'YYYY-MM-DD HH:mm:ss',
  
  // 时间格式
  timeFormat: 'HH:mm:ss',
  
  // 月份格式
  monthFormat: 'YYYY-MM',
  
  // 年份格式
  yearFormat: 'YYYY',
  
  // 日期选择器快捷选项
  shortcuts: [
    {
      text: '今天',
      value: () => new Date()
    },
    {
      text: '昨天',
      value: () => {
        const date = new Date()
        date.setTime(date.getTime() - 3600 * 1000 * 24)
        return date
      }
    },
    {
      text: '一周前',
      value: () => {
        const date = new Date()
        date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
        return date
      }
    }
  ]
}

// 请求配置
export const requestConfig = {
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // 是否携带 token
  withToken: true,
  
  // token 在请求头中的字段名
  tokenKey: 'Authorization',
  
  // token 前缀
  tokenPrefix: 'Bearer ',
  
  // 是否显示请求进度条
  showProgress: true,
  
  // 是否显示错误提示
  showError: true,
  
  // 是否显示成功提示
  showSuccess: false,
  
  // 重试次数
  retryCount: 0,
  
  // 重试延迟（毫秒）
  retryDelay: 1000
}

// 缓存配置
export const cacheConfig = {
  // 默认缓存时间（秒）
  defaultExpire: 3600,
  
  // 缓存前缀
  prefix: 'ruoyi_',
  
  // 是否启用缓存
  enabled: true,
  
  // 缓存类型：localStorage / sessionStorage
  type: 'localStorage'
}

// 主题配置
export const themeConfig = {
  // 默认主题
  defaultTheme: 'light',
  
  // 可选主题
  themes: ['light', 'dark'],
  
  // 主题色
  primaryColor: '#409EFF',
  
  // 成功色
  successColor: '#67C23A',
  
  // 警告色
  warningColor: '#E6A23C',
  
  // 危险色
  dangerColor: '#F56C6C',
  
  // 信息色
  infoColor: '#909399'
}

// 路由配置
export const routerConfig = {
  // 路由模式：hash / history
  mode: 'history',
  
  // 基础路径
  base: '/',
  
  // 是否启用路由懒加载
  lazyLoad: true,
  
  // 是否启用路由缓存
  keepAlive: true,
  
  // 白名单（不需要登录的页面）
  whiteList: ['/login', '/register', '/404', '/401']
}

// 权限配置
export const permissionConfig = {
  // 是否启用权限验证
  enabled: true,
  
  // 权限验证模式：role / permission
  mode: 'permission',
  
  // 超级管理员角色
  superRole: 'admin',
  
  // 默认角色
  defaultRole: 'common'
}

// 导出所有配置
export default {
  app: appConfig,
  table: tableConfig,
  form: formConfig,
  dialog: dialogConfig,
  upload: uploadConfig,
  date: dateConfig,
  request: requestConfig,
  cache: cacheConfig,
  theme: themeConfig,
  router: routerConfig,
  permission: permissionConfig
}

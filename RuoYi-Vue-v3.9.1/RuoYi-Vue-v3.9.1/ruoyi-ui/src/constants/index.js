/**
 * 全局常量定义
 */

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 30, 50, 100],
  LAYOUT: 'total, sizes, prev, pager, next, jumper'
}

// 日期格式
export const DATE_FORMAT = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm:ss',
  MONTH: 'YYYY-MM',
  YEAR: 'YYYY'
}

// 文件上传配置
export const UPLOAD = {
  MAX_SIZE: 10, // MB
  ALLOWED_TYPES: {
    IMAGE: ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'],
    DOCUMENT: ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'],
    VIDEO: ['mp4', 'avi', 'mov', 'wmv'],
    AUDIO: ['mp3', 'wav', 'ogg']
  }
}

// 表单验证规则
export const FORM_RULES = {
  REQUIRED: { required: true, message: '此项为必填项', trigger: 'blur' },
  EMAIL: {
    type: 'email',
    message: '请输入正确的邮箱地址',
    trigger: ['blur', 'change']
  },
  PHONE: {
    pattern: /^1[3-9]\d{9}$/,
    message: '请输入正确的手机号码',
    trigger: 'blur'
  },
  URL: {
    type: 'url',
    message: '请输入正确的URL地址',
    trigger: ['blur', 'change']
  }
}

// 状态常量
export const STATUS = {
  ENABLE: '0',
  DISABLE: '1'
}

// 是否常量
export const YES_NO = {
  YES: 1,
  NO: 0
}

// 性别常量
export const GENDER = {
  MALE: '0',
  FEMALE: '1',
  UNKNOWN: '2'
}

// 菜单类型
export const MENU_TYPE = {
  DIRECTORY: 'M',
  MENU: 'C',
  BUTTON: 'F'
}

// 操作类型
export const OPERATION_TYPE = {
  ADD: 'add',
  EDIT: 'edit',
  DELETE: 'delete',
  VIEW: 'view',
  EXPORT: 'export',
  IMPORT: 'import'
}

// HTTP 状态码
export const HTTP_STATUS = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}

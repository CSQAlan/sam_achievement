/**
 * 通用工具函数
 */

/**
 * 深拷贝对象
 * @param {*} obj 要拷贝的对象
 * @returns {*} 拷贝后的对象
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') {
    return obj
  }

  if (obj instanceof Date) {
    return new Date(obj.getTime())
  }

  if (obj instanceof Array) {
    return obj.map(item => deepClone(item))
  }

  if (obj instanceof Object) {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 防抖函数
 * @param {Function} func 要执行的函数
 * @param {number} wait 等待时间（毫秒）
 * @param {boolean} immediate 是否立即执行
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, wait = 300, immediate = false) {
  let timeout
  return function (...args) {
    const context = this
    const later = function () {
      timeout = null
      if (!immediate) func.apply(context, args)
    }
    const callNow = immediate && !timeout
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    if (callNow) func.apply(context, args)
  }
}

/**
 * 节流函数
 * @param {Function} func 要执行的函数
 * @param {number} wait 等待时间（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(func, wait = 300) {
  let timeout
  let previous = 0
  return function (...args) {
    const context = this
    const now = Date.now()
    const remaining = wait - (now - previous)
    if (remaining <= 0 || remaining > wait) {
      if (timeout) {
        clearTimeout(timeout)
        timeout = null
      }
      previous = now
      func.apply(context, args)
    } else if (!timeout) {
      timeout = setTimeout(() => {
        previous = Date.now()
        timeout = null
        func.apply(context, args)
      }, remaining)
    }
  }
}

/**
 * 生成唯一 ID
 * @param {string} prefix 前缀
 * @returns {string} 唯一 ID
 */
export function generateId(prefix = 'id') {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

/**
 * 数组去重
 * @param {Array} arr 要去重的数组
 * @param {string} key 对象数组的唯一键
 * @returns {Array} 去重后的数组
 */
export function uniqueArray(arr, key) {
  if (!Array.isArray(arr)) {
    return []
  }

  if (key) {
    const seen = new Set()
    return arr.filter(item => {
      const k = item[key]
      if (seen.has(k)) {
        return false
      }
      seen.add(k)
      return true
    })
  }

  return [...new Set(arr)]
}

/**
 * 数组分组
 * @param {Array} arr 要分组的数组
 * @param {string|Function} key 分组键或函数
 * @returns {Object} 分组后的对象
 */
export function groupBy(arr, key) {
  if (!Array.isArray(arr)) {
    return {}
  }

  return arr.reduce((result, item) => {
    const groupKey = typeof key === 'function' ? key(item) : item[key]
    if (!result[groupKey]) {
      result[groupKey] = []
    }
    result[groupKey].push(item)
    return result
  }, {})
}

/**
 * 对象转 URL 参数
 * @param {Object} obj 对象
 * @returns {string} URL 参数字符串
 */
export function objectToQueryString(obj) {
  if (!obj || typeof obj !== 'object') {
    return ''
  }

  return Object.keys(obj)
    .filter(key => obj[key] !== null && obj[key] !== undefined && obj[key] !== '')
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
    .join('&')
}

/**
 * URL 参数转对象
 * @param {string} search URL 参数字符串
 * @returns {Object} 对象
 */
export function queryStringToObject(search) {
  if (!search) {
    return {}
  }

  const str = search.startsWith('?') ? search.slice(1) : search
  return str.split('&').reduce((result, item) => {
    const [key, value] = item.split('=')
    if (key) {
      result[decodeURIComponent(key)] = decodeURIComponent(value || '')
    }
    return result
  }, {})
}

/**
 * 格式化文件大小
 * @param {number} bytes 字节数
 * @param {number} decimals 小数位数
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes, decimals = 2) {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}

/**
 * 下载文件
 * @param {string} url 文件 URL
 * @param {string} filename 文件名
 */
export function downloadFile(url, filename) {
  const link = document.createElement('a')
  link.href = url
  link.download = filename || 'download'
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/**
 * 复制文本到剪贴板
 * @param {string} text 要复制的文本
 * @returns {Promise<boolean>} 是否成功
 */
export async function copyToClipboard(text) {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      return true
    } else {
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      try {
        document.execCommand('copy')
        textArea.remove()
        return true
      } catch (error) {
        textArea.remove()
        return false
      }
    }
  } catch (error) {
    console.error('复制失败:', error)
    return false
  }
}

/**
 * 获取数据类型
 * @param {*} value 要检测的值
 * @returns {string} 数据类型
 */
export function getType(value) {
  return Object.prototype.toString.call(value).slice(8, -1).toLowerCase()
}

/**
 * 判断是否为空
 * @param {*} value 要判断的值
 * @returns {boolean} 是否为空
 */
export function isEmpty(value) {
  if (value === null || value === undefined) {
    return true
  }

  if (typeof value === 'string') {
    return value.trim() === ''
  }

  if (Array.isArray(value)) {
    return value.length === 0
  }

  if (typeof value === 'object') {
    return Object.keys(value).length === 0
  }

  return false
}

/**
 * 延迟执行
 * @param {number} ms 延迟时间（毫秒）
 * @returns {Promise} Promise 对象
 */
export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * 重试函数
 * @param {Function} fn 要执行的函数
 * @param {number} retries 重试次数
 * @param {number} delay 延迟时间（毫秒）
 * @returns {Promise} Promise 对象
 */
export async function retry(fn, retries = 3, delay = 1000) {
  try {
    return await fn()
  } catch (error) {
    if (retries <= 0) {
      throw error
    }
    await sleep(delay)
    return retry(fn, retries - 1, delay)
  }
}

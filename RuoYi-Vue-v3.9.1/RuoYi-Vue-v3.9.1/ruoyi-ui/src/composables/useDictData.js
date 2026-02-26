/**
 * 字典数据 Composable
 * 简化字典数据的使用
 */
import { getCurrentInstance } from 'vue'

export function useDictData(...dictTypes) {
  const { proxy } = getCurrentInstance()

  if (!proxy || !proxy.useDict) {
    console.warn('useDictData: useDict is not available')
    return {}
  }

  return proxy.useDict(...dictTypes)
}

/**
 * 获取字典标签
 */
export function getDictLabel(dictData, value) {
  if (!dictData || !Array.isArray(dictData)) {
    return ''
  }

  const item = dictData.find(dict => dict.value === value)
  return item ? item.label : value
}

/**
 * 获取字典值
 */
export function getDictValue(dictData, label) {
  if (!dictData || !Array.isArray(dictData)) {
    return ''
  }

  const item = dictData.find(dict => dict.label === label)
  return item ? item.value : label
}

/**
 * 字典数据转换为选项格式
 */
export function dictToOptions(dictData, config = {}) {
  const {
    labelKey = 'label',
    valueKey = 'value',
    disabledKey = 'disabled'
  } = config

  if (!dictData || !Array.isArray(dictData)) {
    return []
  }

  return dictData.map(item => ({
    label: item[labelKey],
    value: item[valueKey],
    disabled: item[disabledKey] || false,
    ...item
  }))
}

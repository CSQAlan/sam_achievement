import request from '@/utils/request'

// 根据字典类型查询字典数据信息 (包含所有字段)
export function getFullDicts(dictType) {
  return request({
    url: '/system/dict/data/type/' + dictType,
    method: 'get'
  })
}

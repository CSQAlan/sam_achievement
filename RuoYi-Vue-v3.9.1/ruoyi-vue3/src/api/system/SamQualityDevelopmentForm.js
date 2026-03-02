import request from '@/utils/request'

// 查询素质提升列表
export function listSamQualityDevelopmentForm(query) {
  return request({
    url: '/system/SamQualityDevelopmentForm/list',
    method: 'get',
    params: query
  })
}

// 查询素质提升详细
export function getSamQualityDevelopmentForm(id) {
  return request({
    url: '/system/SamQualityDevelopmentForm/' + id,
    method: 'get'
  })
}

// 新增素质提升
export function addSamQualityDevelopmentForm(data) {
  return request({
    url: '/system/SamQualityDevelopmentForm',
    method: 'post',
    data: data
  })
}

// 修改素质提升
export function updateSamQualityDevelopmentForm(data) {
  return request({
    url: '/system/SamQualityDevelopmentForm',
    method: 'put',
    data: data
  })
}

// 删除素质提升
export function delSamQualityDevelopmentForm(id) {
  return request({
    url: '/system/SamQualityDevelopmentForm/' + id,
    method: 'delete'
  })
}

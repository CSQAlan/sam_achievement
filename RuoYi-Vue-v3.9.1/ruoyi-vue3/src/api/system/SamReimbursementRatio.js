import request from '@/utils/request'

// 查询报销比例列表
export function listSamReimbursementRatio(query) {
  return request({
    url: '/system/SamReimbursementRatio/list',
    method: 'get',
    params: query
  })
}

// 查询报销比例详细
export function getSamReimbursementRatio(id) {
  return request({
    url: '/system/SamReimbursementRatio/' + id,
    method: 'get'
  })
}

// 新增报销比例
export function addSamReimbursementRatio(data) {
  return request({
    url: '/system/SamReimbursementRatio',
    method: 'post',
    data: data
  })
}

// 修改报销比例
export function updateSamReimbursementRatio(data) {
  return request({
    url: '/system/SamReimbursementRatio',
    method: 'put',
    data: data
  })
}

// 删除报销比例
export function delSamReimbursementRatio(id) {
  return request({
    url: '/system/SamReimbursementRatio/' + id,
    method: 'delete'
  })
}

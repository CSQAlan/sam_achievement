import request from '@/utils/request'

// 查询报销项目列表
export function listSamReimbursementItems(query) {
  return request({
    url: '/system/SamReimbursementItems/list',
    method: 'get',
    params: query
  })
}

// 查询报销项目详细
export function getSamReimbursementItems(id) {
  return request({
    url: '/system/SamReimbursementItems/' + id,
    method: 'get'
  })
}

// 新增报销项目
export function addSamReimbursementItems(data) {
  return request({
    url: '/system/SamReimbursementItems',
    method: 'post',
    data: data
  })
}

// 修改报销项目
export function updateSamReimbursementItems(data) {
  return request({
    url: '/system/SamReimbursementItems',
    method: 'put',
    data: data
  })
}

// 删除报销项目
export function delSamReimbursementItems(id) {
  return request({
    url: '/system/SamReimbursementItems/' + id,
    method: 'delete'
  })
}

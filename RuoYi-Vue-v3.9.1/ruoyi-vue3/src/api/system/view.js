import request from '@/utils/request'

// 查询报销项目关联成果列表
export function listView(query) {
  return request({
    url: '/reimbursement/view/list',
    method: 'get',
    params: query
  })
}

// 查询报销项目关联成果详细
export function getView(id) {
  return request({
    url: '/reimbursement/view/' + id,
    method: 'get'
  })
}

// 新增报销项目关联成果
export function addView(data) {
  return request({
    url: '/reimbursement/view',
    method: 'post',
    data: data
  })
}

// 修改报销项目关联成果
export function updateView(data) {
  return request({
    url: '/reimbursement/view',
    method: 'put',
    data: data
  })
}

// 删除报销项目关联成果
export function delView(id) {
  return request({
    url: '/reimbursement/view/' + id,
    method: 'delete'
  })
}

// 查询关联成果列表
export function listAchievement(query) {
  return request({
    url: '/reimbursement/achievement/list',
    method: 'get',
    params: query
  })
}

// 根据报销项目ID查询
export function listByReimbursementId(reimbursementId, query) {
  return request({
    url: '/reimbursement/achievement/listByReimbursement/' + reimbursementId,
    method: 'get',
    params: query
  })
}
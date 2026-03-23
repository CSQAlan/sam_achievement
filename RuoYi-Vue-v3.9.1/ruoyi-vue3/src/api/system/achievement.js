import request from '@/utils/request'

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

// 查询详情
export function getAchievement(id) {
  return request({
    url: '/reimbursement/achievement/' + id,
    method: 'get'
  })
}

// 以下方法保留但实际不会使用（返回错误提示）
export function addAchievement(data) {
  return request({
    url: '/reimbursement/achievement',
    method: 'post',
    data
  })
}

export function updateAchievement(data) {
  return request({
    url: '/reimbursement/achievement',
    method: 'put',
    data
  })
}

export function delAchievement(ids) {
  return request({
    url: '/reimbursement/achievement/' + ids,
    method: 'delete'
  })
}
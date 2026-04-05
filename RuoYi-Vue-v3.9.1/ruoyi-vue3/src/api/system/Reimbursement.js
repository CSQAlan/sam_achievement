import request from '@/utils/request'


// 查询报销项目详情详细
export function getReimbursement(achievementId) {
  return request({
    url: '/system/Reimbursement/' + achievementId,
    method: 'get'
  })
}

// 新增报销项目详情
export function addReimbursement(data) {
  return request({
    url: '/system/Reimbursement',
    method: 'post',
    data: data
  })
}

// 修改报销项目详情
export function updateReimbursement(data) {
  return request({
    url: '/system/Reimbursement',
    method: 'put',
    data: data
  })
}

// 删除报销项目详情
export function delReimbursement(achievementId) {
  return request({
    url: '/system/Reimbursement/' + achievementId,
    method: 'delete'
  })
}
// 查询报销项目详情列表（支持按报销项目ID查询）
export function listReimbursement(query) {
  return request({
    url: '/system/Reimbursement/list',
    method: 'get',
    params: query  // 现在支持传递 reimbursementItemId
  })
}

// 计算报销金额
export function recalculateReimbursementAmount(reimbursementItemId) {
  return request({
    url: '/system/Reimbursement/recalculateAmount',
    method: 'post',
    params: { reimbursementItemId: reimbursementItemId }
  })
}

// 查询未关联的成果列表
export function listUnassociatedProduct(query) {
  return request({
    url: '/system/Reimbursement/unassociatedList',
    method: 'get',
    params: query
  })
}

// 批量关联成果
export function associateAchievements(achievementIds, reimbursementItemId) {
  return request({
    url: '/system/Reimbursement/associateAchievements',
    method: 'post',
    data: { achievementIds, reimbursementItemId }
  })
}

// 取消关联成果
export function cancelAssociation(achievementId, reimbursementItemId) {
  return request({
    url: '/system/Reimbursement/cancelAssociation',
    method: 'post',
    data: { achievementId, reimbursementItemId }
  })
}

// 批量取消关联成果
export function batchCancelAssociation(achievementIds, reimbursementItemId) {
  return request({
    url: '/system/Reimbursement/batchCancelAssociation',
    method: 'post',
    data: { achievementIds, reimbursementItemId }
  })
}
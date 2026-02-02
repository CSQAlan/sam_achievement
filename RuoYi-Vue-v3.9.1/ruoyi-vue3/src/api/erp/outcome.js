import request from '@/utils/request'

// 查询成果管理列表
export function listOutcome(query) {
  return request({
    url: '/erp/outcome/list',
    method: 'get',
    params: query
  })
}

// 查询成果管理详细
export function getOutcome(id) {
  return request({
    url: '/erp/outcome/' + id,
    method: 'get'
  })
}

// 新增成果管理
export function addOutcome(data) {
  return request({
    url: '/erp/outcome',
    method: 'post',
    data: data
  })
}

// 修改成果管理
export function updateOutcome(data) {
  return request({
    url: '/erp/outcome',
    method: 'put',
    data: data
  })
}

// 删除成果管理
export function delOutcome(id) {
  return request({
    url: '/erp/outcome/' + id,
    method: 'delete'
  })
}

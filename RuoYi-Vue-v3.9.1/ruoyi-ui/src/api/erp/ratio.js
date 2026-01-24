import request from '@/utils/request'

// 查询报销比例列表
export function listRatio(query) {
  return request({
    url: '/erp/ratio/list',
    method: 'get',
    params: query
  })
}

// 查询报销比例详细
export function getRatio(id) {
  return request({
    url: '/erp/ratio/' + id,
    method: 'get'
  })
}

// 新增报销比例
export function addRatio(data) {
  return request({
    url: '/erp/ratio',
    method: 'post',
    data: data
  })
}

// 修改报销比例
export function updateRatio(data) {
  return request({
    url: '/erp/ratio',
    method: 'put',
    data: data
  })
}

// 删除报销比例
export function delRatio(id) {
  return request({
    url: '/erp/ratio/' + id,
    method: 'delete'
  })
}

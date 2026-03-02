import request from '@/utils/request'

// 查询成果录入列表
export function listManage(query) {
  return request({
    url: '/achievement/manage/list',
    method: 'get',
    params: query
  })
}

// 查询成果录入详细
export function getManage(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'get'
  })
}

// 新增成果录入
export function addManage(data) {
  return request({
    url: '/achievement/manage',
    method: 'post',
    data: data
  })
}

// 修改成果录入
export function updateManage(data) {
  return request({
    url: '/achievement/manage',
    method: 'put',
    data: data
  })
}

// 删除成果录入
export function delManage(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'delete'
  })
}

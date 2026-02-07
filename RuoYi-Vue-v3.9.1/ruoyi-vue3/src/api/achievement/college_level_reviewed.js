import request from '@/utils/request'

// 查询院级已审核列表
export function listCollege_level_reviewed(query) {
  return request({
    url: '/achievement/college_level_reviewed/list',
    method: 'get',
    params: query
  })
}

// 查询院级已审核详细
export function getCollege_level_reviewed(achievementId) {
  return request({
    url: '/achievement/college_level_reviewed/' + achievementId,
    method: 'get'
  })
}

// 新增院级已审核
export function addCollege_level_reviewed(data) {
  return request({
    url: '/achievement/college_level_reviewed',
    method: 'post',
    data: data
  })
}

// 修改院级已审核
export function updateCollege_level_reviewed(data) {
  return request({
    url: '/achievement/college_level_reviewed',
    method: 'put',
    data: data
  })
}

// 删除院级已审核
export function delCollege_level_reviewed(achievementId) {
  return request({
    url: '/achievement/college_level_reviewed/' + achievementId,
    method: 'delete'
  })
}

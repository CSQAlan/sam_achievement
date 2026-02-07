import request from '@/utils/request'

// 查询院级未审核列表
export function listCollege_level_unreviewed(query) {
  return request({
    url: '/achievement/college_level_unreviewed/list',
    method: 'get',
    params: query
  })
}

// 查询院级未审核详细
export function getCollege_level_unreviewed(achievementId) {
  return request({
    url: '/achievement/college_level_unreviewed/' + achievementId,
    method: 'get'
  })
}

// 新增院级未审核
export function addCollege_level_unreviewed(data) {
  return request({
    url: '/achievement/college_level_unreviewed',
    method: 'post',
    data: data
  })
}

// 修改院级未审核
export function updateCollege_level_unreviewed(data) {
  return request({
    url: '/achievement/college_level_unreviewed',
    method: 'put',
    data: data
  })
}

// 删除院级未审核
export function delCollege_level_unreviewed(achievementId) {
  return request({
    url: '/achievement/college_level_unreviewed/' + achievementId,
    method: 'delete'
  })
}

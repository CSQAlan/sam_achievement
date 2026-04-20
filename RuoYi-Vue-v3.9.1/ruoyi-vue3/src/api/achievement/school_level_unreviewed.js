import request from '@/utils/request'

// 查询校级未审核列表
export function listSchool_level_unreviewed(query) {
  return request({
    url: '/achievement/school_level_unreviewed/list',
    method: 'get',
    params: query
  })
}

// 查询校级未审核详细
export function getSchool_level_unreviewed(achievementId) {
  return request({
    url: '/achievement/school_level_unreviewed/' + achievementId,
    method: 'get'
  })
}

// 新增校级未审核
export function addSchool_level_unreviewed(data) {
  return request({
    url: '/achievement/school_level_unreviewed',
    method: 'post',
    data: data
  })
}

// 修改校级未审核
export function updateSchool_level_unreviewed(data) {
  return request({
    url: '/achievement/school_level_unreviewed',
    method: 'put',
    data: data
  })
}

// 删除校级未审核
export function delSchool_level_unreviewed(achievementId) {
  return request({
    url: '/achievement/school_level_unreviewed/' + achievementId,
    method: 'delete'
  })
}

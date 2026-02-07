import request from '@/utils/request'

// 查询校级已审核列表
export function listSchool_level_reviewed(query) {
  return request({
    url: '/achievement/school_level_reviewed/list',
    method: 'get',
    params: query
  })
}

// 查询校级已审核详细
export function getSchool_level_reviewed(achievementId) {
  return request({
    url: '/achievement/school_level_reviewed/' + achievementId,
    method: 'get'
  })
}

// 新增校级已审核
export function addSchool_level_reviewed(data) {
  return request({
    url: '/achievement/school_level_reviewed',
    method: 'post',
    data: data
  })
}

// 修改校级已审核
export function updateSchool_level_reviewed(data) {
  return request({
    url: '/achievement/school_level_reviewed',
    method: 'put',
    data: data
  })
}

// 删除校级已审核
export function delSchool_level_reviewed(achievementId) {
  return request({
    url: '/achievement/school_level_reviewed/' + achievementId,
    method: 'delete'
  })
}

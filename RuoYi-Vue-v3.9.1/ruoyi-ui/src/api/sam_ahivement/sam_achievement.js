import request from '@/utils/request'

// 查询成果主表列表
export function listSam_achievement(query) {
  return request({
    url: '/sam_ahivement/sam_achievement/list',
    method: 'get',
    params: query
  })
}

// 查询成果主表详细
export function getSam_achievement(achievementId) {
  return request({
    url: '/sam_ahivement/sam_achievement/' + achievementId,
    method: 'get'
  })
}

// 新增成果主表
export function addSam_achievement(data) {
  return request({
    url: '/sam_ahivement/sam_achievement',
    method: 'post',
    data: data
  })
}

// 修改成果主表
export function updateSam_achievement(data) {
  return request({
    url: '/sam_ahivement/sam_achievement',
    method: 'put',
    data: data
  })
}

// 删除成果主表
export function delSam_achievement(achievementId) {
  return request({
    url: '/sam_ahivement/sam_achievement/' + achievementId,
    method: 'delete'
  })
}

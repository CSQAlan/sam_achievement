import request from '@/utils/request'

// 查询素质提升列表
export function listSamQualityDevelopmentForm(query) {
  return request({
    url: '/system/SamQualityDevelopmentForm/list',
    method: 'get',
    params: query
  })
}

// 查询素质提升详细
export function getSamQualityDevelopmentForm(id) {
  return request({
    url: '/system/SamQualityDevelopmentForm/' + id,
    method: 'get'
  })
}

// 新增素质提升
export function addSamQualityDevelopmentForm(data) {
  return request({
    url: '/system/SamQualityDevelopmentForm',
    method: 'post',
    data: data
  })
}

// 修改素质提升
export function updateSamQualityDevelopmentForm(data) {
  return request({
    url: '/system/SamQualityDevelopmentForm',
    method: 'put',
    data: data
  })
}

// 删除素质提升
export function delSamQualityDevelopmentForm(id) {
  return request({
    url: '/system/SamQualityDevelopmentForm/' + id,
    method: 'delete'
  })
}

// 查询带有指定标签的赛事列表
export function listCompetitionByTag(tagName) {
  return request({
    url: '/competition/competition/listByTag',
    method: 'get',
    params: { tagName: tagName }
  })
}

// 查询素质提升奖成果列表
export function listQualityAchievement(query) {
  return request({
    url: '/achievement/manage/list-quality',
    method: 'get',
    params: query
  })
}

// 获取成果详情
export function getAchievementInfo(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'get'
  })
}

// 导出素质提升奖成果列表
export function exportQualityAchievement(query) {
  return request({
    url: '/achievement/manage/export-quality',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

import request from '@/utils/request'

export function listManage(query) {
  return request({
    url: '/achievement/manage/list',
    method: 'get',
    params: query
  })
}

export function getManage(achievementId, params = {}) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'get',
    params: params
  })
}

export function addManage(data) {
  return request({
    url: '/achievement/manage',
    method: 'post',
    data: data
  })
}

export function updateManage(data) {
  return request({
    url: '/achievement/manage',
    method: 'put',
    data: data
  })
}

export function delManage(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'delete'
  })
}

export function listParticipatedAchievement(query) {
  return request({
    url: '/achievement/manage/list-participated',
    method: 'get',
    params: query
  })
}

export function listGuidedAchievement(query) {
  return request({
    url: '/achievement/manage/list-guided',
    method: 'get',
    params: query
  })
}

export function listResponsibleAchievement(query) {
  return request({
    url: '/achievement/manage/list-responsible',
    method: 'get',
    params: query
  })
}

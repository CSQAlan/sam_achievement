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

// 删除成果录入
export function delManage(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'delete'
  })
}

// 根据比赛和届次查询已有的赛道
export function listTracks(competitionId, sessionId) {
  return request({
    url: '/achievement/manage/listTracks',
    method: 'get',
    params: {
      competitionId,
      sessionId
    }
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
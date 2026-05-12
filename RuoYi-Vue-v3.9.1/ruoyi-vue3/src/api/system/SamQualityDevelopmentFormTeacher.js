import request from '@/utils/request'

export function listQualityAchievementTeacher(query) {
  return request({
    url: '/achievement/manage/list-quality-teacher',
    method: 'get',
    params: query
  })
}

export function getAchievementInfo(achievementId) {
  return request({
    url: '/achievement/manage/' + achievementId,
    method: 'get'
  })
}
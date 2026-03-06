import request from '@/utils/request'

// 查询赛事申请列表
export function listCompetitionapply(query) {
  return request({
    url: '/competition-apply/competitionapply/list',
    method: 'get',
    params: query
  })
}

// 查询赛事申请详细
export function getCompetitionapply(id) {
  return request({
    url: '/competition-apply/competitionapply/' + id,
    method: 'get'
  })
}

// api/competition-apply/competitionapply.js
export function addCompetitionapply(data) {
  return request({
    url: '/competition-apply/competitionapply',
    method: 'post',
    data: data // 纯 JSON，无需 FormData
  })
}

// 修改赛事申请
export function updateCompetitionapply(data) {
  return request({
    url: '/competition-apply/competitionapply',
    method: 'put',
    data: data
  })
}

// 删除赛事申请
export function delCompetitionapply(id) {
  return request({
    url: '/competition-apply/competitionapply/' + id,
    method: 'delete'
  })
}
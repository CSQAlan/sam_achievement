import request from '@/utils/request'

// 查询总赛事列表
export function listCompetition(query) {
  return request({
    url: '/competition/competition/list',
    method: 'get',
    params: query
  })
}

// 查询总赛事详细
export function getCompetition(id) {
  return request({
    url: '/competition/competition/' + id,
    method: 'get'
  })
}

// 新增总赛事
export function addCompetition(data) {
  return request({
    url: '/competition/competition',
    method: 'post',
    data: data
  })
}

// 修改总赛事
export function updateCompetition(data) {
  return request({
    url: '/competition/competition',
    method: 'put',
    data: data
  })
}

// 删除总赛事
export function delCompetition(id) {
  return request({
    url: '/competition/competition/' + id,
    method: 'delete'
  })
}

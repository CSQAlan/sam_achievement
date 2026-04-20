import request from '@/utils/request'

// 查询赛事届次列表
export function listTest_session(query) {
  return request({
    url: '/test_competition/test_session/list',
    method: 'get',
    params: query
  })
}

// 查询赛事届次详细
export function getTest_session(id) {
  return request({
    url: '/test_competition/test_session/' + id,
    method: 'get'
  })
}

// 新增赛事届次
export function addTest_session(data) {
  return request({
    url: '/test_competition/test_session',
    method: 'post',
    data: data
  })
}

// 修改赛事届次
export function updateTest_session(data) {
  return request({
    url: '/test_competition/test_session',
    method: 'put',
    data: data
  })
}

// 删除赛事届次
export function delTest_session(id) {
  return request({
    url: '/test_competition/test_session/' + id,
    method: 'delete'
  })
}

import request from '@/utils/request'

// 查询赛事届次列表
export function listSession(query) {
  return request({
    url: '/session/session/list',
    method: 'get',
    params: query
  })
}

// 查询赛事届次详细
export function getSession(id) {
  return request({
    url: '/session/session/' + id,
    method: 'get'
  })
}

// 新增赛事届次
export function addSession(data) {
  return request({
    url: '/session/session',
    method: 'post',
    data: data
  })
}

// 修改赛事届次
export function updateSession(data) {
  return request({
    url: '/session/session',
    method: 'put',
    data: data
  })
}

// 删除赛事届次
export function delSession(id) {
  return request({
    url: '/session/session/' + id,
    method: 'delete'
  })
}

// 新增：批量导入接口
export function importSessionData(data) {
  return request({
    url: '/session/session/importData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data' // 文件上传必须指定该请求头
    }
  })
}

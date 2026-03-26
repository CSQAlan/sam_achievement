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

// 新增：导出模板接口
export function exportTemplateSession() {
  return request({
    url: '/session/session/exportTemplate',
    method: 'post',
    responseType: 'blob' // 二进制流（Excel文件）
  })
}

// 新增：批量导入接口（FormData格式，文件上传）
export function importDataSession(data) {
  return request({
    url: '/session/session/importData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data' // 表单提交格式
    }
  })
}

// 批量修改届次状态（如：预录 -> 启用）
export function updateSessionStatusByIds(data) {
  return request({
    url: '/session/session/status',
    method: 'put',
    data: data
  })
}

// 批量复制届次模板（每条必须重新上传参赛通知PDF）
export function batchCopySession(data) {
  return request({
    url: '/session/session/batchCopy',
    method: 'post',
    data: data
  })
}

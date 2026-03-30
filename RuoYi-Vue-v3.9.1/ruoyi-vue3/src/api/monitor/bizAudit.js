import request from '@/utils/request'

export function listBizAudit(query) {
  return request({
    url: '/monitor/bizAudit/list',
    method: 'get',
    params: query
  })
}

export function getBizAudit(id) {
  return request({
    url: '/monitor/bizAudit/' + id,
    method: 'get'
  })
}

export function delBizAudit(id) {
  return request({
    url: '/monitor/bizAudit/' + id,
    method: 'delete'
  })
}

export function cleanBizAudit(days) {
  return request({
    url: '/monitor/bizAudit/clean',
    method: 'delete',
    params: { days }
  })
}

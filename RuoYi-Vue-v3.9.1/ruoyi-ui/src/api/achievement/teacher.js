import request from '@/utils/request'

// 查询教师档案列表
export function listTeacher(query) {
  return request({
    url: '/achievement/teacher/list',
    method: 'get',
    params: query
  })
}

// 查询教师档案详细
export function getTeacher(id) {
  return request({
    url: '/achievement/teacher/' + id,
    method: 'get'
  })
}

// 新增教师档案
export function addTeacher(data) {
  return request({
    url: '/achievement/teacher',
    method: 'post',
    data: data
  })
}

// 修改教师档案
export function updateTeacher(data) {
  return request({
    url: '/achievement/teacher',
    method: 'put',
    data: data
  })
}

// 删除教师档案
export function delTeacher(id) {
  return request({
    url: '/achievement/teacher/' + id,
    method: 'delete'
  })
}

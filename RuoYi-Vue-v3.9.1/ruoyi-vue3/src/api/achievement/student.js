import request from '@/utils/request'

// 查询学生档案列表
export function listStudent(query) {
  return request({
    url: '/student/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生档案详细
export function getStudent(studentId) {
  return request({
    url: '/student/student/' + studentId,
    method: 'get'
  })
}

// 新增学生档案
export function addStudent(data) {
  return request({
    url: '/student/student',
    method: 'post',
    data: data
  })
}

// 修改学生档案
export function updateStudent(data) {
  return request({
    url: '/student/student',
    method: 'put',
    data: data
  })
}

// 删除学生档案
export function delStudent(studentId) {
  return request({
    url: '/student/student/' + studentId,
    method: 'delete'
  })
}

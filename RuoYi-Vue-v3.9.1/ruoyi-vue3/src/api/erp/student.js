import request from '@/utils/request'

// 查询学生档案列表
export function listStudent(query) {
  return request({
    url: '/erp/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生档案详细
export function getStudent(id) {
  return request({
    url: '/erp/student/' + id,
    method: 'get'
  })
}

// 新增学生档案
export function addStudent(data) {
  return request({
    url: '/erp/student',
    method: 'post',
    data: data
  })
}

// 修改学生档案
export function updateStudent(data) {
  return request({
    url: '/erp/student',
    method: 'put',
    data: data
  })
}

// 删除学生档案
export function delStudent(id) {
  return request({
    url: '/erp/student/' + id,
    method: 'delete'
  })
}

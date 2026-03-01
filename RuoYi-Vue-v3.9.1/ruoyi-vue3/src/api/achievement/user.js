import request from '@/utils/request'

// 查询用户中心列表
export function listUser(query) {
  return request({
    url: '/achievement/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户中心详细
export function getUser(userId) {
  return request({
    url: '/achievement/user/' + userId,
    method: 'get'
  })
}

// 新增用户中心
export function addUser(data) {
  return request({
    url: '/achievement/user',
    method: 'post',
    data: data
  })
}

// 修改用户中心
export function updateUser(data) {
  return request({
    url: '/import request from \'@/utils/request\'\n' +
        'import { parseStrEmpty } from "@/utils/ruoyi";\n' +
        '\n' +
        '// 查询用户列表\n' +
        'export function listUser(query) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/list\',\n' +
        '    method: \'get\',\n' +
        '    params: query\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 查询用户详细\n' +
        'export function getUser(userId) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/\' + parseStrEmpty(userId),\n' +
        '    method: \'get\'\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 新增用户\n' +
        'export function addUser(data) {\n' +
        '  return request({\n' +
        '    url: \'/system/user\',\n' +
        '    method: \'post\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 修改用户\n' +
        'export function updateUser(data) {\n' +
        '  return request({\n' +
        '    url: \'/system/user\',\n' +
        '    method: \'put\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 删除用户\n' +
        'export function delUser(userId) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/\' + userId,\n' +
        '    method: \'delete\'\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 用户密码重置\n' +
        'export function resetUserPwd(userId, password) {\n' +
        '  const data = {\n' +
        '    userId,\n' +
        '    password\n' +
        '  }\n' +
        '  return request({\n' +
        '    url: \'/system/user/resetPwd\',\n' +
        '    method: \'put\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 用户状态修改\n' +
        'export function changeUserStatus(userId, status) {\n' +
        '  const data = {\n' +
        '    userId,\n' +
        '    status\n' +
        '  }\n' +
        '  return request({\n' +
        '    url: \'/system/user/changeStatus\',\n' +
        '    method: \'put\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 查询用户个人信息\n' +
        'export function getUserProfile() {\n' +
        '  return request({\n' +
        '    url: \'/system/user/profile\',\n' +
        '    method: \'get\'\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 修改用户个人信息\n' +
        'export function updateUserProfile(data) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/profile\',\n' +
        '    method: \'put\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 用户密码重置\n' +
        'export function updateUserPwd(oldPassword, newPassword) {\n' +
        '  const data = {\n' +
        '    oldPassword,\n' +
        '    newPassword\n' +
        '  }\n' +
        '  return request({\n' +
        '    url: \'/system/user/profile/updatePwd\',\n' +
        '    method: \'put\',\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 用户头像上传\n' +
        'export function uploadAvatar(data) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/profile/avatar\',\n' +
        '    method: \'post\',\n' +
        '    headers: { \'Content-Type\': \'application/x-www-form-urlencoded\' },\n' +
        '    data: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 查询授权角色\n' +
        'export function getAuthRole(userId) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/authRole/\' + userId,\n' +
        '    method: \'get\'\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 保存授权角色\n' +
        'export function updateAuthRole(data) {\n' +
        '  return request({\n' +
        '    url: \'/system/user/authRole\',\n' +
        '    method: \'put\',\n' +
        '    params: data\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 查询部门下拉树结构\n' +
        'export function deptTreeSelect() {\n' +
        '  return request({\n' +
        '    url: \'/system/user/deptTree\',\n' +
        '    method: \'get\'\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 导入学生数据\n' +
        'export function importStudentData(data) {\n' +
        '  return request({\n' +
        '    url: \'/sam/student/importStudentData\',\n' +
        '    method: \'post\',\n' +
        '    data: data,\n' +
        '    headers: { \'Content-Type\': \'multipart/form-data\' }\n' +
        '  })\n' +
        '}\n' +
        '\n' +
        '// 导入教师数据\n' +
        'export function importTeacherData(data) {\n' +
        '  return request({\n' +
        '    url: \'/sam/teacher/importTeacherData\',\n' +
        '    method: \'post\',\n' +
        '    data: data,\n' +
        '    headers: { \'Content-Type\': \'multipart/form-data\' }\n' +
        '  })\n' +
        '}',
    method: 'put',
    data: data
  })
}

// 删除用户中心
export function delUser(userId) {
  return request({
    url: '/achievement/user/' + userId,
    method: 'delete'
  })
}

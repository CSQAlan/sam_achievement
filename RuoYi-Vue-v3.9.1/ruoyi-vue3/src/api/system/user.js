import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 鏌ヨ鐢ㄦ埛鍒楄〃
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 鏌ヨ鐢ㄦ埛璇︾粏
export function getUser(userId) {
  return request({
    url: '/system/user/' + parseStrEmpty(userId),
    method: 'get'
  })
}

// 鏂板鐢ㄦ埛
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

// 淇敼鐢ㄦ埛
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data: data
  })
}

// 鍒犻櫎鐢ㄦ埛
export function delUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'delete'
  })
}

// 鐢ㄦ埛瀵嗙爜閲嶇疆
export function resetUserPwd(userId, password) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 鐢ㄦ埛鐘舵€佷慨鏀?
export function changeUserStatus(userId, status) {
  const data = {
    userId,
    status
  }
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data: data
  })
}

// 鏌ヨ鐢ㄦ埛涓汉淇℃伅
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

// 淇敼鐢ㄦ埛涓汉淇℃伅
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

// 查询个人中心部门树
export function getProfileDeptTree() {
  return request({
    url: '/system/user/profile/deptTree',
    method: 'get'
  })
}

// 鐢ㄦ埛瀵嗙爜閲嶇疆
export function updateUserPwd(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: data
  })
}

// 鐢ㄦ埛澶村儚涓婁紶
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: data
  })
}

// 鏌ヨ鎺堟潈瑙掕壊
export function getAuthRole(userId) {
  return request({
    url: '/system/user/authRole/' + userId,
    method: 'get'
  })
}

// 淇濆瓨鎺堟潈瑙掕壊
export function updateAuthRole(data) {
  return request({
    url: '/system/user/authRole',
    method: 'put',
    params: data
  })
}

// 鏌ヨ閮ㄩ棬涓嬫媺鏍戠粨鏋?
export function deptTreeSelect() {
  return request({
    url: '/system/user/deptTree',
    method: 'get'
  })
}
// 瀵煎叆瀛︾敓鏁版嵁
export function importStudentData(data) {
    return request({
        url: '/achievement/student/importStudentData',
        method: 'post',
        data: data,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

// 瀵煎叆鏁欏笀鏁版嵁
export function importTeacherData(data) {
    return request({
        url: '/achievement/teacher/importTeacherData',
        method: 'post',
        data: data,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}


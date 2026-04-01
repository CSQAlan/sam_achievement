import request from '@/utils/request'

// List departments
export function listDept(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params: query
  })
}

// List departments excluding child nodes
export function listDeptExcludeChild(deptId) {
  return request({
    url: '/system/dept/list/exclude/' + deptId,
    method: 'get'
  })
}

// Get department detail
export function getDept(deptId) {
  return request({
    url: '/system/dept/' + deptId,
    method: 'get'
  })
}

// Add department
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

// Update department
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

// Delete department
export function delDept(deptId) {
  return request({
    url: '/system/dept/' + deptId,
    method: 'delete'
  })
}

// Import department data
export function importDeptData(data, updateSupport) {
  return request({
    url: '/system/dept/importData?updateSupport=' + updateSupport,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Validate import data before importing
export function checkDeptImport(data, updateSupport) {
  return request({
    url: '/system/dept/importCheck?updateSupport=' + updateSupport,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Export failed import details
export function exportDeptImportFailDetails(data, updateSupport) {
  return request({
    url: '/system/dept/importFailDetails?updateSupport=' + updateSupport,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' },
    responseType: 'blob'
  })
}

// Download department import template
export function importDeptTemplate() {
  return request({
    url: '/system/dept/importTemplate',
    method: 'post',
    responseType: 'blob'
  })
}

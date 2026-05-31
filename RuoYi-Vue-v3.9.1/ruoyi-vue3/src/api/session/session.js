import request from "@/utils/request";

export function listSession(query) {
  return request({
    url: "/session/session/list",
    method: "get",
    params: query,
  });
}

export function allIdsSession(query) {
  return request({
    url: "/session/session/query/allIds",
    method: "get",
    params: query,
  });
}

export function getSession(id) {
  return request({
    url: `/session/session/${id}`,
    method: "get",
  });
}

export function addSession(data) {
  return request({
    url: "/session/session",
    method: "post",
    data,
  });
}

export function updateSession(data) {
  return request({
    url: "/session/session",
    method: "put",
    data,
  });
}

export function delSession(id) {
  return request({
    url: `/session/session/${id}`,
    method: "delete",
  });
}

export function exportTemplateSession() {
  return request({
    url: "/session/session/exportTemplate",
    method: "post",
    responseType: "blob",
  });
}

export function importDataSession(data, updateSupport = false) {
  return request({
    url: `/session/session/importData?updateSupport=${updateSupport}`,
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
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

// 批量修改届次状态（如：预录 -> 启用）
// export function updateSessionStatusByIds(data) {
//   return request({
//     url: '/session/session/status',
//     method: 'put',
//     data: data
//   })
// }

// 批量启用预录届次（事务保护，全部成功或全部回滚）
export function batchEnableSession(data) {
  return request({
    url: '/session/session/batchEnable',
    method: 'post',
    data: data
  })
}

// 为预录届次上传参赛通知（学生/管理员均可）
export function uploadSessionNotice(id, uuid) {
  return request({
    url: `/session/session/${id}/notice`,
    method: 'put',
    data: { uuid }
  })
}

// 批量复制届次模板（每条必须重新上传参赛通知PDF）
// export function batchCopySession(data) {
//   return request({
//     url: '/session/session/batchCopy',
//     method: 'post',
//     data: data
//   })
// }

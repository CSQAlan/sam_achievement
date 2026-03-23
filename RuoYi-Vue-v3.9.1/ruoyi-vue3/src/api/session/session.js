import request from "@/utils/request";

import request from '@/utils/request'

// 查询赛事届次列表
export function listSession(query) {
  return request({
    url: "/session/session/list",
    method: "get",
    params: query,
  });
    url: '/session/session/list',
    method: 'get',
    params: query
  })
}

// 查询赛事届次详细
export function getSession(id) {
  return request({
    url: `/session/session/${id}`,
    method: "get",
  });
}

// 新增赛事届次
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

// 删除赛事届次
export function delSession(id) {
  return request({
    url: `/session/session/${id}`,
    method: "delete",
  });
    url: '/session/session/' + id,
    method: 'delete'
  })
}

// 新增：导出模板接口
export function exportTemplateSession() {
  return request({
    url: "/session/session/exportTemplate",
    method: "post",
    responseType: "blob",
  });
    url: '/session/session/exportTemplate',
    method: 'post',
    responseType: 'blob' // 二进制流（Excel文件）
  })
}

export function importDataSession(data, updateSupport = false) {
// 新增：批量导入接口（FormData格式，文件上传）
export function importDataSession(data) {
  return request({
    url: '/session/session/importData',
    method: 'post',
    data: data,
    url: `/session/session/importData?updateSupport=${updateSupport}`,
    method: "post",
    data,
    headers: {
      'Content-Type': 'multipart/form-data' // 表单提交格式
    }
  })
      "Content-Type": "multipart/form-data",
    },
  });
}

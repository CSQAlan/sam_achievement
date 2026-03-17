import request from "@/utils/request";

export function listSession(query) {
  return request({
    url: "/session/session/list",
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

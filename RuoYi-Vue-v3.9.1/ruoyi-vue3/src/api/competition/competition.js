import request from "@/utils/request";

export function listCompetition(query) {
  return request({
    url: "/competition/competition/list",
    method: "get",
    params: query,
  });
}

export function getCompetition(id) {
  return request({
    url: `/competition/competition/${id}`,
    method: "get",
  });
}

export function addCompetition(data) {
  return request({
    url: "/competition/competition",
    method: "post",
    data,
  });
}

export function updateCompetition(data) {
  return request({
    url: "/competition/competition",
    method: "put",
    data,
  });
}

export function delCompetition(id) {
  return request({
    url: `/competition/competition/${id}`,
    method: "delete",
  });
}

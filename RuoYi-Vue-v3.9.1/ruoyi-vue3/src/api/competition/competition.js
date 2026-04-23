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
// 解析PDF并匹配竞赛
export function analyzeCompetitionPdf(data) {
  return request({
    url: "/competition/competition/import/analyze",
    method: "post",
    data,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

// 确认建立关联
export function linkCompetitionPdf(data) {
  return request({
    url: "/competition/competition/import/link",
    method: "post",
    data,
  });
}

// 手动建立关联并学习别名
export function manualLinkCompetitionPdf(data) {
  return request({
    url: "/competition/competition/import/manualLink",
    method: "post",
    data,
  });
}

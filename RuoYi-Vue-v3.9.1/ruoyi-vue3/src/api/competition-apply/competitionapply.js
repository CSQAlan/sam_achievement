import request from "@/utils/request";

export function listCompetitionapply(query) {
  return request({
    url: "/competition-apply/competitionapply/list",
    method: "get",
    params: query,
  });
}

export function getCompetitionapply(id) {
  return request({
    url: `/competition-apply/competitionapply/${id}`,
    method: "get",
  });
}

export function addCompetitionapply(data) {
  return request({
    url: "/competition-apply/competitionapply",
    method: "post",
    data,
  });
}

export function updateCompetitionapply(data) {
  return request({
    url: "/competition-apply/competitionapply",
    method: "put",
    data,
  });
}

export function delCompetitionapply(id) {
  return request({
    url: `/competition-apply/competitionapply/${id}`,
    method: "delete",
  });
}

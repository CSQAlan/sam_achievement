import request from "@/utils/request";

// 查询赛事申请列表
export function listCompetitionapply(query) {
  return request({
    url: "/competition-apply/competitionapply/list",
    method: "get",
    params: query,
  });
}

// 查询赛事申请详细
export function getCompetitionapply(id) {
  return request({
    url: '/competition-apply/competitionapply/' + id,
    method: 'get'
  })
}

// api/competition-apply/competitionapply.js
export function addCompetitionapply(data) {
  return request({
    url: '/competition-apply/competitionapply',
    method: 'post',
    data: data // 纯 JSON，无需 FormData
  })
}

// 修改赛事申请
export function updateCompetitionapply(data) {
  return request({
    url: '/competition-apply/competitionapply',
    method: 'put',
    data: data
  })
}

// 删除赛事申请
export function delCompetitionapply(id) {
  return request({
    url: '/competition-apply/competitionapply/' + id,
    method: 'delete'
  })
}

// 审核赛事申请（通过/驳回）
export function reviewCompetitionapply(id, data) {
  return request({
    url: `/competition-apply/competitionapply/review/${id}`,
    method: 'put',
    data
  })
}

// ====================== 新增：文件预览相关接口 ======================
/**
 * 1. 查询指定赛事申请的附件列表
 * @param {Number} applyId 赛事申请ID
 * @returns 附件列表数据
 */
export function getAttachmentList(applyId) {
  return request({
    url: `/competition-apply/competitionapply/attachment/list/${applyId}`,
    method: 'get'
  })
}

/**
 * 2. 获取文件预览地址（带token鉴权，用于前端渲染预览）
 * @param {Number} attachmentId 附件ID
 * @returns 完整的预览地址（带token）
 */
export function getPreviewUrl(attachmentId) {
  // 从本地存储获取RuoYi的token（适配框架的权限校验）
  const token = localStorage.getItem('Admin-Token') || ''
  // 拼接预览地址 + token鉴权参数
  return `${process.env.VUE_APP_BASE_API}/competition-apply/competitionapply/attachment/preview/${attachmentId}?token=${token}`
}

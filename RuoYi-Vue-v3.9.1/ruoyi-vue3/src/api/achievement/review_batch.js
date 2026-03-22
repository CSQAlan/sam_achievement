import request from '@/utils/request';

// 单条审核提交：前端只提交必要参数，最终流转由后端统一裁决
export function submitReview(data) {
  return request({
    url: '/achievement/review/submit',
    method: 'put',
    data
  });
}

// 批量审核提交：与单条审核走同一套后端规则
export function batchUpdateReviewStatus(source, data) {
  return request({
    url: '/achievement/review/batchSubmit',
    method: 'put',
    timeout: 60000,
    data: {
      source,
      ...data
    }
  });
}

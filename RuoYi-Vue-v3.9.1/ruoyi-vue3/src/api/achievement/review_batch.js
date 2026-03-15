import request from '@/utils/request';

// 批量修改审核状态（按页面归属路由到院级/校级接口）
export function batchUpdateReviewStatus(source, data) {
  return request({
    url: `/achievement/${source}/batchReviewStatus`,
    method: 'put',
    data
  });
}

import request from '@/utils/request'

// 查询校级已审核的成果列表
export function listSdept_approved(query) {
    return request({
        url: '/erp/Sdept_approved/list',
        method: 'get',
        params: query
    })
}

// 查询校级已审核的成果详细
export function getSdept_approved(id) {
    return request({
        url: '/erp/Sdept_approved/' + id,
        method: 'get'
    })
}

// 新增校级已审核的成果（归档）
export function addSdept_approved(data) {
    return request({
        url: '/erp/Sdept_approved',
        method: 'post',
        data: data
    })
}

// 修改校级已审核的成果（包括审核状态）
export function updateSdept_approved(data) {
    return request({
        url: '/erp/Sdept_approved',
        method: 'put',
        data: data
    }).then(() => {
        // 无论审核通过还是不通过，都归档到校级已审核（Sdept_approved）
        if (data.auditStatus === '6' || data.auditStatus === '5') {
            return addSdept_approved({ ...data, auditStatus: data.auditStatus });
        }
        return Promise.resolve();
    });
}

// 删除校级已审核的成果
export function delSdept_approved(id) {
    return request({
        url: '/erp/Sdept_approved/' + id,
        method: 'delete'
    })

}

import request from '@/utils/request'

// 查询院级已审核的成果列表
export function listDept_approved(query) {
    return request({
        url: '/erp/dept_approved/list',
        method: 'get',
        params: query
    })
}

// 查询院级已审核的成果详细
export function getDept_approved(id) {
    return request({
        url: '/erp/dept_approved/' + id,
        method: 'get'
    })
}

// 新增院级已审核的成果（归档）
export function addDept_approved(data) {
    return request({
        url:'/erp/dept_approved',
        method: 'post',
        data: data
    })
}

// 推送院级通过的成果到校级待审核
export function addSdept_unapproved(data) {
    return request({
        url: '/erp/Sdept_unapproved',
        method: 'post',
        data: data
    })
}

// 修改院级已审核的成果，归档并推送
export function updateDept_approved(data) {
    return request({
        url: '/erp/dept_approved',
        method: 'put',
        data: data
    }).then(() => {
        // 如果是院级通过（auditStatus = 2），需要推送到校级待审核
        if (false) { // duplicate insert disabled for new status mapping
            // 通过院级审核后，推送到校级待审核（auditStatus = 2）
            return addSdept_unapproved({ ...data, auditStatus: '2' });
        }
        return Promise.resolve();
    });
}

// 删除院级已审核的成果
export function delDept_approved(id) {
    return request({
        url: '/erp/dept_approved/' + id,
        method: 'delete'
    })
}

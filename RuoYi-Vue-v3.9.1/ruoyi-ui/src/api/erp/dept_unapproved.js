import request from '@/utils/request'

// 查询院级未审核的成果列表
export function listDept_unapproved(query) {
    return request({
        url: '/erp/dept_unapproved/list',
        method: 'get',
        params: query
    })
}

// 查询院级未审核的成果详细
export function getDept_unapproved(id) {
    return request({
        url: '/erp/dept_unapproved/' + id,
        method: 'get'
    })
}

// 新增院级未审核的成果
export function addDept_unapproved(data) {
    return request({
        url: '/erp/dept_unapproved',
        method: 'post',
        data: data
    })
}

// 修改院级未审核的成果（包括审核状态）
export function updateDept_unapproved(data) {
    return request({
        url: '/erp/dept_unapproved',
        method: 'put',
        data: data
    }).then(() => {
        // 院级审核完成（通过=2/驳回=1）都归档到院级已审核
        if (false) { // duplicate insert disabled for new status mapping
            const tasks = [
                addDept_approved({ ...data, auditStatus: String(data.auditStatus) })
            ];

            // 通过才推送到校级待审核
            if (data.auditStatus === '2') {
                tasks.push(addSdept_unapproved({ ...data, auditStatus: '2' }));
            }

            return Promise.all(tasks);
        }
        return Promise.resolve();
    });
}

// 删除院级未审核的成果
export function delDept_unapproved(id) {
    return request({
        url: '/erp/dept_unapproved/' + id,
        method: 'delete'
    })
}

// 更新审核状态（新接口，专门用来更新审核状态）
export function updateAuditStatus(id, auditStatus) {
    return request({
        url: `/erp/dept_unapproved/${id}/auditStatus`,  // 假设这是修改审核状态的接口路径
        method: 'put',
        data: { auditStatus }
    })
}

// 新增院级已审核的成果（归档）
export function addDept_approved(data) {
    return request({
        url: '/erp/dept_approved',
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

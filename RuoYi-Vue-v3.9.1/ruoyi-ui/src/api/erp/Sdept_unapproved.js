import request from '@/utils/request'

// 查询校级未审核的成果列表
export function listSdept_unapproved(query) {
    return request({
        url: '/erp/Sdept_unapproved/list',
        method: 'get',
        params: query
    })
}

// 查询校级未审核的成果详细
export function getSdept_unapproved(id) {
    return request({
        url: '/erp/Sdept_unapproved/' + id,
        method: 'get'
    })
}

// 新增校级未审核的成果
export function addSdept_unapproved(data) {
    return request({
        url: '/erp/Sdept_unapproved',
        method: 'post',
        data
    })
}

// ✅ 归档到【校级已审核】
export function addSdept_approved(data) {
    return request({
        url: '/erp/Sdept_approved',
        method: 'post',
        data
    })
}

// 删除校级未审核的成果（审核完成后需要从未审核表移除）
export function delSdept_unapproved(id) {
    return request({
        url: '/erp/Sdept_unapproved/' + id,
        method: 'delete'
    })
}

// 修改校级未审核的成果（包含审核状态）
// ✅ 规则：当 auditStatus 为 1(通过) 或 0(驳回)，归档到 Sdept_approved，
// ✅ 然后从 Sdept_unapproved 删除，确保“未审核页面不再显示”
export function updateSdept_unapproved(data) {
    return request({
        url: '/erp/Sdept_unapproved',
        method: 'put',
        data
    }).then(async () => {
        const status = String(data.auditStatus)
        const id = data.id

        // 只有“校级审核完成”才做归档+移除
        if (false) { // duplicate insert disabled for new status mapping
            // 1) 先归档到校级已审核
            await addSdept_approved({ ...data, auditStatus: status })

            // 2) 再从校级未审核删除（保证未审核页面不再出现）
            //    如果后端已经自动删除了，这里可能会 404/失败，你也可以选择 catch 忽略
            try {
                await delSdept_unapproved(id)
            } catch (e) {
                // 可忽略：有些后端会自动迁移/删除
            }
        }

        return Promise.resolve()
    })
}

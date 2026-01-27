package com.ruoyi.erp.mapper;

import java.util.List;
import com.ruoyi.erp.domain.ErpAttachment;

public interface ErpAttachmentMapper
{
    // 根据成果ID查询附件
    public List<ErpAttachment> selectByOutcomeId(Long outcomeId);

    // 新增附件
    public int insertErpAttachment(ErpAttachment erpAttachment);

    // 根据成果ID删除附件
    public int deleteByOutcomeId(Long outcomeId);
}
package com.ruoyi.competitionapply.service;

import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;
import java.util.List;

/**
 * 赛事申请 Service 接口
 */
public interface ICompetitionApplyService
{
    CompetitionApply selectCompetitionApplyById(Long id);

    List<CompetitionApply> selectCompetitionApplyList(CompetitionApply competitionApply);

    @BizAudit(bizType = "competition_apply", bizName = "赛事申请", opType = BizAuditOpType.CREATE, handler = "competitionApplyBizAuditHandler")
    int insertCompetitionApply(CompetitionApply competitionApply);

    @BizAudit(bizType = "competition_apply", bizName = "赛事申请", opType = BizAuditOpType.UPDATE, handler = "competitionApplyBizAuditHandler")
    int updateCompetitionApply(CompetitionApply competitionApply);

    @BizAudit(bizType = "competition_apply", bizName = "赛事申请", opType = BizAuditOpType.BATCH, handler = "competitionApplyBizAuditHandler")
    int deleteCompetitionApplyByIds(Long[] ids);

    @BizAudit(bizType = "competition_apply", bizName = "赛事申请", opType = BizAuditOpType.DELETE, handler = "competitionApplyBizAuditHandler")
    int deleteCompetitionApplyById(Long id);

    CompetitionApplyAttachment selectCompetitionApplyAttachmentById(Long attachmentId);

    List<CompetitionApplyAttachment> selectCompetitionApplyAttachmentListByApplyId(Long applyId);

    @BizAudit(bizType = "competition_apply", bizName = "赛事申请审核", opType = BizAuditOpType.APPROVE, handler = "competitionApplyBizAuditHandler")
    int reviewCompetitionApply(Long id, String status, String auditRemark);
}

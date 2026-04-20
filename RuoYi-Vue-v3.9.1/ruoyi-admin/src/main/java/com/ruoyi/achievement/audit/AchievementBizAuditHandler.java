package com.ruoyi.achievement.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.achievement.domain.ExportAttachmentZipReq;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 成果模块业务审计处理器。
 */
@Component("achievementBizAuditHandler")
public class AchievementBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private SamAchievementMapper samAchievementMapper;

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.ADD || opType == BizAuditOpType.CREATE || opType == BizAuditOpType.IMPORT)
        {
            return null;
        }

        String achievementId = resolveId(context);
        if (StringUtils.isBlank(achievementId))
        {
            String[] achievementIds = resolveIds(context);
            if (achievementIds != null && achievementIds.length > 0)
            {
                return buildBatchSnapshot(achievementIds);
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        if (context.getAnnotation().opType() == BizAuditOpType.DELETE)
        {
            return null;
        }

        String achievementId = resolveId(context);
        if (StringUtils.isBlank(achievementId))
        {
            String[] achievementIds = resolveIds(context);
            if (achievementIds != null && achievementIds.length > 0)
            {
                return buildBatchSnapshot(achievementIds);
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    @Override
    public BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
        String methodName = context.getJoinPoint().getSignature().getName();
        if ("submitReview".equals(methodName) || "batchSubmitReview".equals(methodName))
        {
            String source = resolveSource(context);
            Long reviewStatus = resolveReviewStatus(context);
            if (StringUtils.containsIgnoreCase(source, "college"))
            {
                if (Long.valueOf(2L).equals(reviewStatus))
                {
                    return BizAuditOpType.APPROVE;
                }
                if (Long.valueOf(1L).equals(reviewStatus))
                {
                    return BizAuditOpType.REJECT;
                }
            }
            if (StringUtils.containsIgnoreCase(source, "school"))
            {
                if (Long.valueOf(1L).equals(reviewStatus))
                {
                    return BizAuditOpType.APPROVE;
                }
                if (Long.valueOf(0L).equals(reviewStatus))
                {
                    return BizAuditOpType.REJECT;
                }
            }
        }
        return context.getAnnotation().opType();
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        String achievementId = resolveId(context);
        if (StringUtils.isNotBlank(achievementId))
        {
            return achievementId;
        }

        String[] achievementIds = resolveIds(context);
        if (achievementIds != null && achievementIds.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (String item : achievementIds)
            {
                if (StringUtils.isNotBlank(item))
                {
                    sb.append(item.trim()).append(",");
                }
            }
            return sb.length() > 0 ? sb.deleteCharAt(sb.length() - 1).toString() : null;
        }
        return null;
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        String reviewBizName = resolveReviewBizName(context);
        if (StringUtils.isNotBlank(reviewBizName))
        {
            return reviewBizName;
        }
        if (isAttachmentExportMethod(context))
        {
            return context.getAnnotation().bizName();
        }
        SamAchievement source = resolveAchievementSnapshot(after, before);
        if (source != null && StringUtils.isNotBlank(source.getName()))
        {
            return source.getName();
        }
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        SamAchievement source = resolveAchievementSnapshot(after, before);
        if (source == null)
        {
            return null;
        }
        if (StringUtils.isNotBlank(source.getCertificateNo()))
        {
            return source.getCertificateNo();
        }
        return source.getName();
    }

    @Override
    public Integer resolveBatchFlag(BizAuditContext context, Object before, Object after)
    {
        String[] ids = resolveIds(context);
        return ids != null && ids.length > 1 ? 1 : 0;
    }

    @Override
    public Integer resolveBatchCount(BizAuditContext context, Object before, Object after)
    {
        String[] ids = resolveIds(context);
        return ids != null ? ids.length : null;
    }

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("achievementId", "成果编号");
        map.put("competitionId", "比赛ID");
        map.put("competitionName", "比赛");
        map.put("sessionId", "届次ID");
        map.put("sessionName", "届次");
        map.put("category", "成果类别");
        map.put("name", "作品名称");
        map.put("teamName", "团队名称");
        map.put("contestant", "参赛选手");
        map.put("instructor", "指导老师");
        map.put("level", "获奖级别");
        map.put("grade", "获奖等级");
        map.put("track", "赛道");
        map.put("certificateNo", "证书编号");
        map.put("groupId", "组别");
        map.put("awardTime", "获奖时间");
        map.put("year", "年份");
        map.put("ownerDepId", "归属学院");
        map.put("isReimburse", "是否报销");
        map.put("reimbursementStatus", "报销状态");
        map.put("isSupplement", "是否补录");
        map.put("fee", "报名费");
        map.put("reimbursementFee", "实际报销金额");
        map.put("reimbursementRatio", "报销比例");
        map.put("reimbursementItemId", "报销项目ID");
        map.put("reimbursementDate", "报销发放时间");
        map.put("itemIndex", "报销项目序号");
        map.put("qualityIndex", "素质提升序号");
        map.put("submittedAt", "提交时间");
        map.put("reviewedAt", "院级审核时间");
        map.put("schoolReviewedAt", "校级审核时间");
        map.put("reviewResult", "院级审核结果");
        map.put("schooiReviewResult", "校级审核结果");
        map.put("reviewReason", "院级驳回原因");
        map.put("schoolReviewReason", "校级驳回原因");
        map.put("auditBy", "院级审核人");
        map.put("schoolAuditBy", "校级审核人");
        map.put("samAchievementParticipantList", "参赛选手列表");
        map.put("samAchievementParticipantList.studentId", "选手学号");
        map.put("samAchievementParticipantList.studentName", "选手姓名");
        map.put("samAchievementParticipantList.orderNo", "选手顺序");
        map.put("samAchievementParticipantList.manager", "是否负责人");
        map.put("samAchievementParticipantList.school", "选手学院");
        map.put("samAchievementAdvisorList", "指导老师列表");
        map.put("samAchievementAdvisorList.teacherId", "教师工号");
        map.put("samAchievementAdvisorList.teacherName", "教师姓名");
        map.put("samAchievementAdvisorList.orderNo", "指导顺序");
        map.put("samAchievementAdvisorList.manager", "是否主要指导");
        map.put("samAchievementAttachmentList", "附件列表");
        map.put("samAchievementAttachmentList.type", "附件类型");
        map.put("samAchievementAttachmentList.fileUuid", "附件UUID");
        map.put("samAchievementAttachmentList.fileType", "附件文件类型");
        map.put("batchItems", "批量对象");
        return map;
    }

    @Override
    public Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("category", "achievement_category");
        map.put("level", "award_level_type");
        map.put("grade", "award_rank");
        map.put("groupId", "group_type");
        map.put("isSupplement", "sys_yes_no");
        map.put("reviewResult", "college_audit_status");
        map.put("schooiReviewResult", "school_audit_status");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        return Arrays.asList(
                "achievementId", "name", "competitionName", "sessionName", "category",
                "level", "grade", "track", "certificateNo", "groupId", "awardTime",
                "year", "isSupplement", "teamName", "ownerDepId", "reviewResult", "schooiReviewResult",
                "reviewReason", "schoolReviewReason", "auditBy", "schoolAuditBy",
                "samAchievementParticipantList", "samAchievementAdvisorList", "samAchievementAttachmentList"
        );
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        return new HashSet<String>(Arrays.asList(
                "serialVersionUID", "params", "searchValue", "remark",
                "createBy", "createTime", "updateBy", "updateTime", "delFlag",
                "contestant", "instructor", "reviewStage", "reviewStatus"
        ));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "achievementId");
        map.put("samAchievementParticipantList", "studentId");
        map.put("samAchievementAdvisorList", "teacherId");
        map.put("samAchievementAttachmentList", "attachmentId");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "name");
        map.put("samAchievementParticipantList", "studentName");
        map.put("samAchievementAdvisorList", "teacherName");
        map.put("samAchievementAttachmentList", "fileUuid");
        return map;
    }

    @Override
    public JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        JSONObject extra = new JSONObject();
        extra.put("methodName", context.getJoinPoint().getSignature().getName());
        String source = resolveSource(context);
        if (StringUtils.isNotBlank(source))
        {
            extra.put("source", source);
        }
        Long reviewStatus = resolveReviewStatus(context);
        if (reviewStatus != null)
        {
            extra.put("reviewStatus", reviewStatus);
        }
        List<Integer> attachmentTypes = resolveAttachmentTypes(context);
        if (attachmentTypes != null && !attachmentTypes.isEmpty())
        {
            extra.put("attachmentTypes", attachmentTypes);
        }
        return extra;
    }

    private SamAchievement resolveAchievementSnapshot(Object preferred, Object fallback)
    {
        if (preferred instanceof SamAchievement)
        {
            return (SamAchievement) preferred;
        }
        if (fallback instanceof SamAchievement)
        {
            return (SamAchievement) fallback;
        }
        return null;
    }

    private String resolveId(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null || args.length == 0)
        {
            return null;
        }

        if (isAttachmentExportMethod(context))
        {
            String[] attachmentIds = resolveAttachmentExportIds(args);
            return attachmentIds != null && attachmentIds.length == 1 ? attachmentIds[0] : null;
        }

        String methodName = context.getJoinPoint().getSignature().getName();
        if (("submitReview".equals(methodName) || "batchSubmitReview".equals(methodName))
                && args.length > 1 && args[1] instanceof String)
        {
            return normalizeId((String) args[1]);
        }

        for (Object arg : args)
        {
            if (arg instanceof SamAchievement)
            {
                return normalizeId(((SamAchievement) arg).getAchievementId());
            }
            if (arg instanceof reviewed)
            {
                return normalizeId(((reviewed) arg).getAchievementId());
            }
        }

        if (args.length == 1 && args[0] instanceof String)
        {
            return normalizeId((String) args[0]);
        }
        return null;
    }

    private String[] resolveIds(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null || args.length == 0)
        {
            return null;
        }

        if (isAttachmentExportMethod(context))
        {
            return resolveAttachmentExportIds(args);
        }

        String methodName = context.getJoinPoint().getSignature().getName();
        if ("batchSubmitReview".equals(methodName) && args.length > 1 && args[1] instanceof String[])
        {
            return normalizeIds((String[]) args[1]);
        }

        for (Object arg : args)
        {
            if (arg instanceof String[])
            {
                return normalizeIds((String[]) arg);
            }
        }
        return null;
    }

    private String resolveSource(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null || args.length == 0)
        {
            return null;
        }
        Object first = args[0];
        if (first instanceof String)
        {
            String value = (String) first;
            if (StringUtils.containsIgnoreCase(value, "college") || StringUtils.containsIgnoreCase(value, "school"))
            {
                return value;
            }
        }
        return null;
    }

    private Long resolveReviewStatus(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof Long)
            {
                return (Long) arg;
            }
        }
        return null;
    }

    private Object buildBatchSnapshot(String[] achievementIds)
    {
        List<SamAchievement> list = new ArrayList<SamAchievement>();
        for (String achievementId : achievementIds)
        {
            String normalizedId = normalizeId(achievementId);
            if (StringUtils.isBlank(normalizedId))
            {
                continue;
            }
            SamAchievement achievement = samAchievementMapper.selectSamAchievementByAchievementId(normalizedId);
            if (achievement != null)
            {
                list.add(achievement);
            }
        }
        JSONObject root = new JSONObject();
        root.put("batchItems", list);
        return root;
    }

    private static Object buildArgsSnapshot(Object[] args)
    {
        if (args == null || args.length == 0)
        {
            return null;
        }
        if (args.length == 1 && (args[0] instanceof List || args[0].getClass().isArray()))
        {
            JSONObject wrapper = new JSONObject();
            wrapper.put("batchItems", args[0]);
            return wrapper;
        }
        if (args.length == 1)
        {
            return args[0];
        }
        JSONObject root = new JSONObject();
        for (int i = 0; i < args.length; i++)
        {
            root.put("arg" + i, args[i]);
        }
        return root;
    }

    private String[] normalizeIds(String[] ids)
    {
        if (ids == null)
        {
            return null;
        }
        List<String> normalized = new ArrayList<String>();
        for (String id : ids)
        {
            String item = normalizeId(id);
            if (StringUtils.isNotBlank(item))
            {
                normalized.add(item);
            }
        }
        return normalized.isEmpty() ? null : normalized.toArray(new String[0]);
    }

    private String normalizeId(String achievementId)
    {
        return StringUtils.isBlank(achievementId) ? null : achievementId.trim();
    }

    private boolean isReviewSubmitMethod(BizAuditContext context)
    {
        String methodName = context.getJoinPoint().getSignature().getName();
        return "submitReview".equals(methodName) || "batchSubmitReview".equals(methodName);
    }

    private boolean isAttachmentExportMethod(BizAuditContext context)
    {
        return "exportAttachmentZip".equals(context.getJoinPoint().getSignature().getName());
    }

    private String resolveReviewBizName(BizAuditContext context)
    {
        if (!isReviewSubmitMethod(context))
        {
            return null;
        }

        String source = resolveSource(context);
        Long reviewStatus = resolveReviewStatus(context);
        boolean batch = "batchSubmitReview".equals(context.getJoinPoint().getSignature().getName())
                || (resolveIds(context) != null && resolveIds(context).length > 1);

        String stagePrefix = null;
        if (StringUtils.containsIgnoreCase(source, "college"))
        {
            stagePrefix = "院级";
            if (Long.valueOf(2L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "审核通过";
            }
            if (Long.valueOf(1L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "审核驳回";
            }
            if (Long.valueOf(0L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "退回待审核";
            }
        }
        else if (StringUtils.containsIgnoreCase(source, "school"))
        {
            stagePrefix = "校级";
            if (Long.valueOf(1L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "审核通过";
            }
            if (Long.valueOf(0L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "审核驳回";
            }
            if (Long.valueOf(2L).equals(reviewStatus))
            {
                return (batch ? "批量" : "") + stagePrefix + "退回待审核";
            }
        }

        return stagePrefix == null ? null : (batch ? "批量" : "") + stagePrefix + "提交审核";
    }

    private String[] resolveAttachmentExportIds(Object[] args)
    {
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof ExportAttachmentZipReq)
            {
                List<String> achievementIds = ((ExportAttachmentZipReq) arg).getAchievementIds();
                if (achievementIds == null || achievementIds.isEmpty())
                {
                    return null;
                }
                return normalizeIds(achievementIds.toArray(new String[0]));
            }
        }
        return null;
    }

    private List<Integer> resolveAttachmentTypes(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof ExportAttachmentZipReq)
            {
                return ((ExportAttachmentZipReq) arg).getTypes();
            }
        }
        return null;
    }
}

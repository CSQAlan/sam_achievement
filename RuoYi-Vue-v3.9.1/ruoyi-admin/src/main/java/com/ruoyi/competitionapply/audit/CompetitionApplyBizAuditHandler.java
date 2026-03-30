package com.ruoyi.competitionapply.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.mapper.CompetitionApplyMapper;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 赛事申请业务审计处理器
 * 直接使用 Mapper 避免与 CompetitionApplyServiceImpl（含@BizAudit方法）产生循环依赖
 */
@Component("competitionApplyBizAuditHandler")
public class CompetitionApplyBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private CompetitionApplyMapper competitionApplyMapper;

    // === 快照采集 ===

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.ADD || opType == BizAuditOpType.IMPORT)
        {
            return null; // 新增前无数据
        }

        Long id = resolveId(context);
        if (id == null)
        {
            Long[] ids = resolveIds(context);
            if (ids != null && ids.length > 0)
            {
                ArrayList<CompetitionApply> list = new ArrayList<CompetitionApply>();
                for (Long itemId : ids)
                {
                    CompetitionApply apply = competitionApplyMapper.selectCompetitionApplyById(itemId);
                    if (apply != null) list.add(apply);
                }
                JSONObject root = new JSONObject();
                root.put("batchItems", list);
                return root;
            }
            return null;
        }
        return competitionApplyMapper.selectCompetitionApplyById(id);
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.DELETE)
        {
            return null; // 删除后无数据
        }

        Long id = resolveId(context);
        if (id == null) return null;
        return competitionApplyMapper.selectCompetitionApplyById(id);
    }

    // === 元数据解析 ===

    @Override
    public BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
        // 审核方法动态判断是通过还是驳回
        if ("reviewCompetitionApply".equals(context.getJoinPoint().getSignature().getName()))
        {
            Object[] args = context.getArgs();
            String status = (args != null && args.length > 1) ? String.valueOf(args[1]) : null;
            return "2".equals(status) ? BizAuditOpType.REJECT : BizAuditOpType.APPROVE;
        }
        return context.getAnnotation().opType();
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        Long id = resolveId(context);
        if (id != null) return String.valueOf(id);
        Long[] ids = resolveIds(context);
        if (ids != null && ids.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (Long itemId : ids) sb.append(itemId).append(",");
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
        return null;
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        CompetitionApply source = after instanceof CompetitionApply ? (CompetitionApply) after
                : before instanceof CompetitionApply ? (CompetitionApply) before : null;
        if (source != null && StringUtils.isNotBlank(source.getName())) return source.getName();
        return "赛事申请";
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        CompetitionApply source = after instanceof CompetitionApply ? (CompetitionApply) after
                : before instanceof CompetitionApply ? (CompetitionApply) before : null;
        return source == null ? null : source.getSession();
    }

    @Override
    public Integer resolveBatchFlag(BizAuditContext context, Object before, Object after)
    {
        Long[] ids = resolveIds(context);
        return ids != null && ids.length > 1 ? 1 : 0;
    }

    @Override
    public Integer resolveBatchCount(BizAuditContext context, Object before, Object after)
    {
        Long[] ids = resolveIds(context);
        return ids == null ? null : ids.length;
    }

    // === 字段配置 ===

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        // 使用 LinkedHashMap 保留插入顺序（兜底排序）
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("id",             "申请ID");
        map.put("name",           "赛事名称");
        map.put("session",        "赛事届次");
        map.put("year",           "年度");
        map.put("category",       "赛事类别");
        map.put("level",          "赛事级别");
        map.put("organizations",  "主办单位");
        map.put("scopeType",      "适用范围");
        map.put("tags",           "标签");
        map.put("status",         "审核状态");
        map.put("auditRemark",    "审核意见");
        map.put("memo",           "备注");
        // 附件子表字段
        map.put("competitionApplyAttachmentList",                  "附件明细");
        map.put("competitionApplyAttachmentList.documentName",     "附件名称");
        map.put("competitionApplyAttachmentList.attachmentType",   "附件类型");
        map.put("competitionApplyAttachmentList.uuid",             "附件UUID");
        return map;
    }

    @Override
    public Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("category",  "sys_competition_category");
        map.put("level",     "sys_competition_level");
        map.put("scopeType", "sys_competition_scope_type");
        map.put("status",    "sys_competition_apply_status");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        return Arrays.asList(
                "id", "name", "session", "year",
                "category", "level", "organizations", "scopeType", "tags",
                "status", "auditRemark", "memo",
                "competitionApplyAttachmentList"
        );
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        // 屏蔽内部系统字段和用户无需关心的外键
        return new HashSet<String>(Arrays.asList(
                "competitionId",    // 生成的赛事ID，申请人无需感知
                "sessionId",        // 生成的届次ID
                "applicantUserId",  // 申请人userId（已有操作人信息）
                "applicantDepId",   // 申请人学院ID
                "auditBy",          // 审核人userId（已有操作人信息）
                "auditTime",        // 审核时间（已有操作时间信息）
                "attachmentUrls",   // 前端中间字段，非真实存储
                "serialVersionUID"
        ));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("competitionApplyAttachmentList", "id");
        map.put("batchItems",                     "id");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("competitionApplyAttachmentList", "documentName");
        map.put("batchItems",                     "name");
        return map;
    }

    @Override
    public JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        JSONObject extra = new JSONObject();
        extra.put("methodName", context.getJoinPoint().getSignature().getName());
        // 审核操作额外记录审核状态
        if ("reviewCompetitionApply".equals(context.getJoinPoint().getSignature().getName()))
        {
            Object[] args = context.getArgs();
            if (args != null && args.length > 1) extra.put("reviewStatus", args[1]);
        }
        return extra;
    }

    // === 私有工具 ===

    private Long resolveId(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null) return null;
        // reviewCompetitionApply(Long id, String status, String remark) 的第一个参数是 id
        if ("reviewCompetitionApply".equals(context.getJoinPoint().getSignature().getName())
                && args.length > 0 && args[0] instanceof Long)
        {
            return (Long) args[0];
        }
        for (Object arg : args)
        {
            if (arg instanceof CompetitionApply) return ((CompetitionApply) arg).getId();
            if (arg instanceof Long)             return (Long) arg;
        }
        return null;
    }

    private Long[] resolveIds(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null) return null;
        for (Object arg : args)
        {
            if (arg instanceof Long[]) return (Long[]) arg;
        }
        return null;
    }
}

package com.ruoyi.competition.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.mapper.CompetitionMapper;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 总赛事业务审计处理器
 * 直接使用 Mapper 避免与 CompetitionServiceImpl（含@BizAudit方法）产生循环依赖
 */
@Component("competitionBizAuditHandler")
public class CompetitionBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private CompetitionMapper competitionMapper;

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
                ArrayList<Competition> list = new ArrayList<Competition>();
                for (Long itemId : ids)
                {
                    Competition comp = competitionMapper.selectCompetitionById(itemId);
                    if (comp != null) list.add(comp);
                }
                JSONObject root = new JSONObject();
                root.put("batchItems", list);
                return root;
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return competitionMapper.selectCompetitionById(id);
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
        if (id == null)
        {
            Long[] ids = resolveIds(context);
            if (ids != null && ids.length > 0)
            {
                ArrayList<Competition> list = new ArrayList<Competition>();
                for (Long itemId : ids)
                {
                    Competition comp = competitionMapper.selectCompetitionById(itemId);
                    if (comp != null) list.add(comp);
                }
                JSONObject root = new JSONObject();
                root.put("batchItems", list);
                return root;
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return competitionMapper.selectCompetitionById(id);
    }

    // === 元数据解析 ===

    @Override
    public BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
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
        Competition source = after instanceof Competition ? (Competition) after
                : before instanceof Competition ? (Competition) before : null;
        if (source != null && StringUtils.isNotBlank(source.getName())) return source.getName();
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        Competition source = after instanceof Competition ? (Competition) after
                : before instanceof Competition ? (Competition) before : null;
        return source == null ? null : source.getName();
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
        return ids != null ? ids.length : null;
    }

    // === 字段配置 ===

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        // 保留 id 供查询使用
        map.put("id",            "赛事ID");
        map.put("name",          "赛事名称");
        map.put("category",      "赛事类别");
        map.put("level",         "赛事级别");
        map.put("organizations", "盖章单位");
        map.put("tags",          "标签");
        map.put("status",        "状态");
        map.put("scopeType",     "适用范围");
        map.put("memo",          "备注");
        map.put("batchItems",    "批量对象");
        map.put("competitionDeptRelList",         "关联学院");
        map.put("competitionDeptRelList.deptId",  "部门ID");
        return map;
    }

    @Override
    public Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("category", "sys_competition_category");
        map.put("level",    "sys_competition_level");
        map.put("status",   "sys_competition_status");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        // 按用户可读顺序排列展示（最重要的在前）
        return Arrays.asList(
                "id", "name", "category", "level", "organizations", "tags", "status", "scopeType", "memo"
        );
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        // 只屏蔽真正无意义的技术字段，id 保留
        return new HashSet<String>(Arrays.asList("serialVersionUID"));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems",             "id");
        map.put("competitionDeptRelList", "deptId");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "name");
        return map;
    }

    @Override
    public JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        JSONObject extra = new JSONObject();
        extra.put("methodName", context.getJoinPoint().getSignature().getName());
        return extra;
    }

    // === 私有工具 ===

    private Long resolveId(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null) return null;
        for (Object arg : args)
        {
            if (arg instanceof Competition) return ((Competition) arg).getId();
            if (arg instanceof Long)        return (Long) arg;
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

    private static Object buildArgsSnapshot(Object[] args)
    {
        if (args == null || args.length == 0) return null;
        if (args.length == 1 && (args[0] instanceof List || args[0].getClass().isArray()))
        {
            JSONObject wrapper = new JSONObject();
            wrapper.put("batchItems", args[0]);
            return wrapper;
        }
        if (args.length == 1) return args[0];
        JSONObject root = new JSONObject();
        for (int i = 0; i < args.length; i++) root.put("arg" + i, args[i]);
        return root;
    }
}

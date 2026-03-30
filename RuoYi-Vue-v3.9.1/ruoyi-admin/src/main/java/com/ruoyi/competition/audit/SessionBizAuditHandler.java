package com.ruoyi.competition.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.competition.domain.Session;
import com.ruoyi.competition.mapper.SessionMapper;
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
 * 赛事届次业务审计处理器
 * 直接使用 Mapper 避免与 SessionServiceImpl（含@BizAudit方法）产生循环依赖
 */
@Component("sessionBizAuditHandler")
public class SessionBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private SessionMapper sessionMapper;

    // === 快照采集 ===

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.ADD || opType == BizAuditOpType.CREATE || opType == BizAuditOpType.IMPORT)
        {
            return null; // 新增/导入前无数据
        }

        Long id = resolveId(context);
        if (id == null)
        {
            Long[] ids = resolveIds(context);
            if (ids != null && ids.length > 0)
            {
                ArrayList<Session> list = new ArrayList<Session>();
                for (Long itemId : ids)
                {
                    Session s = sessionMapper.selectSessionById(itemId);
                    if (s != null) list.add(s);
                }
                JSONObject root = new JSONObject();
                root.put("batchItems", list);
                return root;
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return sessionMapper.selectSessionById(id);
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
                ArrayList<Session> list = new ArrayList<Session>();
                for (Long itemId : ids)
                {
                    Session s = sessionMapper.selectSessionById(itemId);
                    if (s != null) list.add(s);
                }
                JSONObject root = new JSONObject();
                root.put("batchItems", list);
                return root;
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return sessionMapper.selectSessionById(id);
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
        Session source = after instanceof Session ? (Session) after
                : before instanceof Session ? (Session) before : null;
        if (source != null && StringUtils.isNotBlank(source.getSession())) return source.getSession();
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        Session source = after instanceof Session ? (Session) after
                : before instanceof Session ? (Session) before : null;
        return source == null ? null : source.getSession();
    }

    @Override
    public Integer resolveBatchFlag(BizAuditContext context, Object before, Object after)
    {
        Long[] ids = resolveIds(context);
        if (ids != null && ids.length > 1) return 1;
        Object[] args = context.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof List) return 1;
        return 0;
    }

    @Override
    public Integer resolveBatchCount(BizAuditContext context, Object before, Object after)
    {
        Long[] ids = resolveIds(context);
        if (ids != null) return ids.length;
        Object[] args = context.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof List) return ((List<?>) args[0]).size();
        return null;
    }

    // === 字段配置 ===

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        // 保留 id 供查询使用
        map.put("id",            "届次ID");
        map.put("competitionId", "所属赛事ID");
        map.put("competitionName","赛事名称");
        map.put("session",       "届次");
        map.put("year",          "年份");
        map.put("category",      "赛事类别");
        map.put("level",         "赛事级别");
        map.put("organizations", "盖章单位");
        map.put("tags",          "标签");
        map.put("status",        "状态");
        map.put("uuid",          "通知附件UUID");
        map.put("batchItems",    "批量对象");
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
        return Arrays.asList(
                "id", "competitionId", "competitionName", "session", "year",
                "category", "level", "organizations", "tags", "status", "uuid"
        );
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        // templateSessionId 是内部复制标记，不需展示
        return new HashSet<String>(Arrays.asList("templateSessionId", "serialVersionUID"));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "id");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "session");
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
            if (arg instanceof Session) return ((Session) arg).getId();
            if (arg instanceof Long)    return (Long) arg;
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

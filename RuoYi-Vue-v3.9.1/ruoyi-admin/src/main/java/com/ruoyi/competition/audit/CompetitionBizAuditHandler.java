package com.ruoyi.competition.audit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.domain.CompetitionDeptRel;
import com.ruoyi.competition.mapper.CompetitionMapper;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import com.ruoyi.system.service.ISysDeptService;
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
 * 总赛事业务审计处理器
 * 直接使用 Mapper 避免与 CompetitionServiceImpl（含@BizAudit方法）产生循环依赖
 */
@Component("competitionBizAuditHandler")
public class  CompetitionBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private ISysDeptService deptService;

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.ADD || opType == BizAuditOpType.IMPORT)
        {
            return null;
        }

        Long id = resolveId(context);
        if (id == null)
        {
            Long[] ids = resolveIds(context);
            if (ids != null && ids.length > 0)
            {
                return buildBatchSnapshot(ids);
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return buildCompetitionSnapshot(competitionMapper.selectCompetitionById(id));
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.DELETE)
        {
            return null;
        }

        Long id = resolveId(context);
        if (id == null)
        {
            Long[] ids = resolveIds(context);
            if (ids != null && ids.length > 0)
            {
                return buildBatchSnapshot(ids);
            }
            return buildArgsSnapshot(context.getArgs());
        }
        return buildCompetitionSnapshot(competitionMapper.selectCompetitionById(id));
    }

    @Override
    public BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
        return context.getAnnotation().opType();
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        Long id = resolveId(context);
        if (id != null)
        {
            return String.valueOf(id);
        }
        Long[] ids = resolveIds(context);
        if (ids != null && ids.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (Long itemId : ids)
            {
                sb.append(itemId).append(",");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
        return null;
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        String name = extractCompetitionName(after);
        if (StringUtils.isBlank(name))
        {
            name = extractCompetitionName(before);
        }
        if (StringUtils.isNotBlank(name))
        {
            return name;
        }
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        String name = extractCompetitionName(after);
        return StringUtils.isNotBlank(name) ? name : extractCompetitionName(before);
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

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("id", "赛事ID");
        map.put("name", "赛事名称");
        map.put("category", "赛事类别");
        map.put("level", "赛事级别");
        map.put("organizations", "盖章单位");
        map.put("tags", "标签");
        map.put("scopeType", "适用范围");
        map.put("status", "状态");
        map.put("deptNames", "归属学院");
        map.put("memo", "备注");
        map.put("batchItems", "批量对象");
        map.put("competitionDeptRelList", "归属学院明细");
        map.put("competitionDeptRelList.deptId", "学院ID");
        map.put("competitionDeptRelList.deptName", "学院名称");
        map.put("competitionDeptRelList.sort", "排序");
        return map;
    }

    @Override
    public Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("category", "sys_competition_category");
        map.put("level", "sys_competition_level");
        map.put("tags", "sys_competition_tag");
        map.put("scopeType", "sys_competition_scope_type");
        map.put("status", "sys_competition_status");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        return Arrays.asList(
                "id", "name", "category", "level", "organizations", "tags", "scopeType", "status", "deptNames", "memo"
        );
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        return new HashSet<String>(Arrays.asList(
                "serialVersionUID",
                "categoryList",
                "levelList",
                "tagsList"
        ));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems", "id");
        map.put("competitionDeptRelList", "deptId");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems", "name");
        map.put("competitionDeptRelList", "deptName");
        return map;
    }

    @Override
    public JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        JSONObject extra = new JSONObject();
        extra.put("methodName", context.getJoinPoint().getSignature().getName());
        return extra;
    }

    private Long resolveId(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof Competition)
            {
                return ((Competition) arg).getId();
            }
            if (arg instanceof Long)
            {
                return (Long) arg;
            }
        }
        return null;
    }

    private Long[] resolveIds(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof Long[])
            {
                return (Long[]) arg;
            }
        }
        return null;
    }

    private JSONObject buildBatchSnapshot(Long[] ids)
    {
        JSONArray items = new JSONArray();
        for (Long itemId : ids)
        {
            Object snapshot = buildCompetitionSnapshot(competitionMapper.selectCompetitionById(itemId));
            if (snapshot != null)
            {
                items.add(snapshot);
            }
        }
        JSONObject root = new JSONObject();
        root.put("batchItems", items);
        return root;
    }

    private Object buildCompetitionSnapshot(Competition competition)
    {
        if (competition == null)
        {
            return null;
        }
        JSONObject snapshot = (JSONObject) JSON.toJSON(competition);
        List<CompetitionDeptRel> relList = competition.getCompetitionDeptRelList();
        if (relList == null || relList.isEmpty())
        {
            return snapshot;
        }

        JSONArray relSnapshots = new JSONArray();
        List<String> deptNames = new ArrayList<String>();
        for (CompetitionDeptRel rel : relList)
        {
            JSONObject relSnapshot = (JSONObject) JSON.toJSON(rel);
            String deptName = resolveDeptName(rel.getDeptId());
            if (StringUtils.isNotBlank(deptName))
            {
                relSnapshot.put("deptName", deptName);
                if (!deptNames.contains(deptName))
                {
                    deptNames.add(deptName);
                }
            }
            relSnapshots.add(relSnapshot);
        }
        snapshot.put("competitionDeptRelList", relSnapshots);
        if (StringUtils.isBlank(snapshot.getString("deptNames")) && !deptNames.isEmpty())
        {
            snapshot.put("deptNames", String.join("、", deptNames));
        }
        return snapshot;
    }

    private String resolveDeptName(Long deptId)
    {
        if (deptId == null)
        {
            return null;
        }
        SysDept dept = deptService.selectDeptById(deptId);
        return dept == null ? null : dept.getDeptName();
    }

    private String extractCompetitionName(Object source)
    {
        if (source instanceof Competition)
        {
            return ((Competition) source).getName();
        }
        if (source instanceof JSONObject)
        {
            return ((JSONObject) source).getString("name");
        }
        return null;
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
}

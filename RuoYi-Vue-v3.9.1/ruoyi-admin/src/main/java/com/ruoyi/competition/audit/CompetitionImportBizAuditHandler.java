package com.ruoyi.competition.audit;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.competition.domain.Session;
import com.ruoyi.competition.mapper.SessionMapper;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.BizAuditContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 竞赛导入业务审计处理器 (届次级别)
 */
@Component("competitionImportBizAuditHandler")
public class CompetitionImportBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        List<Long> ids = resolveIds(context);
        if (ids == null || ids.isEmpty())
        {
            return null;
        }
        return buildBatchSnapshot(ids);
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        List<Long> ids = resolveIds(context);
        if (ids == null || ids.isEmpty())
        {
            return null;
        }
        return buildBatchSnapshot(ids);
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        List<Long> ids = resolveIds(context);
        if (ids != null && !ids.isEmpty())
        {
            return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
        return null;
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        Object[] args = context.getArgs();
        String yearStr = "";
        if (args != null && args.length > 3 && args[3] instanceof Integer)
        {
            yearStr = " [" + args[3] + "年]";
        }
        
        if (args != null && args.length > 2 && args[2] instanceof String)
        {
            return "竞赛导入" + yearStr + ": " + args[2];
        }
        return "竞赛导入" + yearStr;
    }

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems", "批量对象");
        map.put("batchItems.id", "届次ID");
        map.put("batchItems.competitionName", "赛事名称");
        map.put("batchItems.session", "届次");
        map.put("batchItems.year", "年份");
        map.put("batchItems.tags", "标签");
        return map;
    }

    @Override
    public Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems.tags", "sys_competition_tag");
        return map;
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems", "id");
        return map;
    }

    @Override
    public Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("batchItems", "competitionName");
        return map;
    }

    @SuppressWarnings("unchecked")
    private List<Long> resolveIds(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof List)
        {
            return (List<Long>) args[0];
        }
        return null;
    }

    private JSONObject buildBatchSnapshot(List<Long> ids)
    {
        JSONArray items = new JSONArray();
        for (Long id : ids)
        {
            Session sess = sessionMapper.selectSessionById(id);
            if (sess != null)
            {
                JSONObject snapshot = new JSONObject();
                snapshot.put("id", sess.getId());
                snapshot.put("competitionName", sess.getCompetitionName());
                snapshot.put("session", sess.getSession());
                snapshot.put("year", sess.getYear());
                snapshot.put("tags", sess.getTags());
                items.add(snapshot);
            }
        }
        JSONObject root = new JSONObject();
        root.put("batchItems", items);
        return root;
    }
}

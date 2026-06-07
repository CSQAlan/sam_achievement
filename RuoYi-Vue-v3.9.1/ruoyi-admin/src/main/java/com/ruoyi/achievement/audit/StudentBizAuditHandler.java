package com.ruoyi.achievement.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.mapper.SamStudentMapper;
import com.ruoyi.common.core.domain.AjaxResult;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("studentBizAuditHandler")
public class StudentBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private SamStudentMapper samStudentMapper;

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.ADD || opType == BizAuditOpType.CREATE || opType == BizAuditOpType.IMPORT)
        {
            return null;
        }

        Long studentId = resolveStudentId(context);
        if (studentId != null)
        {
            return samStudentMapper.selectSamStudentByStudentId(studentId);
        }

        Long[] ids = resolveStudentIds(context);
        if (ids != null && ids.length > 0)
        {
            return buildBatchSnapshot(ids);
        }

        String no = resolveStudentNo(context);
        if (StringUtils.isNotBlank(no))
        {
            return samStudentMapper.selectSamStudentByNo(no);
        }
        return buildArgsSnapshot(context.getArgs());
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        if (isImportMethod(context))
        {
            return buildImportSnapshot(methodResult, context);
        }
        if (context.getAnnotation().opType() == BizAuditOpType.DELETE)
        {
            return null;
        }

        Long studentId = resolveStudentId(context);
        if (studentId != null)
        {
            return samStudentMapper.selectSamStudentByStudentId(studentId);
        }

        Long[] ids = resolveStudentIds(context);
        if (ids != null && ids.length > 0)
        {
            return buildBatchSnapshot(ids);
        }

        String no = resolveStudentNo(context);
        if (StringUtils.isNotBlank(no))
        {
            return samStudentMapper.selectSamStudentByNo(no);
        }
        return buildArgsSnapshot(context.getArgs());
    }

    @Override
    public BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
        BizAuditOpType opType = context.getAnnotation().opType();
        if (opType == BizAuditOpType.UPDATE && before == null && after != null)
        {
            return BizAuditOpType.CREATE;
        }
        return opType;
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        Long studentId = resolveStudentId(context);
        if (studentId != null)
        {
            return String.valueOf(studentId);
        }

        Long[] ids = resolveStudentIds(context);
        if (ids != null && ids.length > 0)
        {
            StringBuilder sb = new StringBuilder();
            for (Long id : ids)
            {
                sb.append(id).append(",");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }

        String no = resolveStudentNo(context);
        return StringUtils.isBlank(no) ? null : no;
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        if (isImportMethod(context))
        {
            return context.getAnnotation().bizName();
        }
        Long[] ids = resolveStudentIds(context);
        if (context.getAnnotation().opType() == BizAuditOpType.DELETE && ids != null && ids.length > 1)
        {
            return "批量删除学生档案";
        }
        SamStudent source = after instanceof SamStudent ? (SamStudent) after : before instanceof SamStudent ? (SamStudent) before : null;
        if (source != null && StringUtils.isNotBlank(source.getName()))
        {
            return source.getName();
        }
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        SamStudent source = after instanceof SamStudent ? (SamStudent) after : before instanceof SamStudent ? (SamStudent) before : null;
        return source == null ? null : source.getNo();
    }

    @Override
    public Integer resolveBatchFlag(BizAuditContext context, Object before, Object after)
    {
        if (isImportMethod(context))
        {
            return 1;
        }
        Long[] ids = resolveStudentIds(context);
        return ids != null && ids.length > 1 ? 1 : 0;
    }

    @Override
    public Integer resolveBatchCount(BizAuditContext context, Object before, Object after)
    {
        if (isImportMethod(context))
        {
            return after instanceof JSONObject ? ((JSONObject) after).getInteger("totalCount") : null;
        }
        Long[] ids = resolveStudentIds(context);
        return ids == null ? null : ids.length;
    }

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("studentId", "学生ID");
        map.put("name", "学生姓名");
        map.put("no", "学号");
        map.put("schoolName", "学院");
        map.put("departmentName", "院系");
        map.put("majorName", "专业");
        map.put("className", "班级");
        map.put("classYear", "年级");
        map.put("totalCount", "导入总数");
        map.put("successCount", "导入成功数");
        map.put("failureCount", "导入失败数");
        map.put("updateSupport", "更新已存在数据");
        map.put("batchItems", "批量对象");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        return Arrays.asList("studentId", "name", "no", "schoolName", "departmentName", "majorName", "classYear", "className",
                "totalCount", "successCount", "failureCount", "updateSupport");
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        return new HashSet<String>(Arrays.asList(
                "serialVersionUID", "params", "searchValue", "remark",
                "createBy", "createTime", "updateBy", "updateTime",
                "school", "department", "major", "message"
        ));
    }

    @Override
    public Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("batchItems", "studentId");
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
        if (isImportMethod(context))
        {
            extra.put("updateSupport", resolveUpdateSupport(context));
            if (after instanceof JSONObject)
            {
                JSONObject snapshot = (JSONObject) after;
                extra.put("totalCount", snapshot.getInteger("totalCount"));
                extra.put("successCount", snapshot.getInteger("successCount"));
                extra.put("failureCount", snapshot.getInteger("failureCount"));
                extra.put("message", snapshot.getString("message"));
            }
        }
        return extra;
    }

    private boolean isImportMethod(BizAuditContext context)
    {
        return "importData".equals(context.getJoinPoint().getSignature().getName());
    }

    private boolean resolveUpdateSupport(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return false;
        }
        for (Object arg : args)
        {
            if (arg instanceof Boolean)
            {
                return (Boolean) arg;
            }
        }
        return false;
    }

    private JSONObject buildImportSnapshot(Object methodResult, BizAuditContext context)
    {
        JSONObject snapshot = new JSONObject();
        snapshot.put("updateSupport", resolveUpdateSupport(context));
        if (methodResult instanceof AjaxResult)
        {
            Object msgObject = ((AjaxResult) methodResult).get(AjaxResult.MSG_TAG);
            String message = msgObject == null ? null : String.valueOf(msgObject);
            snapshot.put("message", message);

            Integer totalCount = extractCount(message, "共计\\s*(\\d+)\\s*条");
            if (totalCount == null)
            {
                totalCount = extractCount(message, "共\\s*(\\d+)\\s*条");
            }
            Integer successCount = extractCount(message, "成功\\s*(\\d+)\\s*条");
            Integer failureCount = extractCount(message, "失败\\s*(\\d+)\\s*条");

            if (successCount == null && totalCount != null)
            {
                successCount = totalCount;
            }
            if (failureCount == null)
            {
                failureCount = 0;
            }
            if (totalCount == null && successCount != null)
            {
                totalCount = successCount + failureCount;
            }

            snapshot.put("totalCount", totalCount);
            snapshot.put("successCount", successCount);
            snapshot.put("failureCount", failureCount);
        }
        return snapshot;
    }

    private Integer extractCount(String text, String regex)
    {
        if (StringUtils.isBlank(text))
        {
            return null;
        }
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find())
        {
            return Integer.valueOf(matcher.group(1));
        }
        return null;
    }

    private Object buildBatchSnapshot(Long[] ids)
    {
        List<SamStudent> list = new ArrayList<SamStudent>();
        for (Long id : ids)
        {
            SamStudent item = samStudentMapper.selectSamStudentByStudentId(id);
            if (item != null)
            {
                list.add(item);
            }
        }
        JSONObject root = new JSONObject();
        root.put("batchItems", list);
        return root;
    }

    private Long resolveStudentId(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof SamStudent)
            {
                return ((SamStudent) arg).getStudentId();
            }
            if (arg instanceof Long)
            {
                return (Long) arg;
            }
        }
        return null;
    }

    private Long[] resolveStudentIds(BizAuditContext context)
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

    private String resolveStudentNo(BizAuditContext context)
    {
        Object[] args = context.getArgs();
        if (args == null)
        {
            return null;
        }
        for (Object arg : args)
        {
            if (arg instanceof SamStudent)
            {
                return ((SamStudent) arg).getNo();
            }
        }
        return null;
    }

    private static Object buildArgsSnapshot(Object[] args)
    {
        if (args == null || args.length == 0)
        {
            return null;
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
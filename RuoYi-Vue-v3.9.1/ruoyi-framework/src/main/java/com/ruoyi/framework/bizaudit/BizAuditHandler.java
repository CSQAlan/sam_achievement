package com.ruoyi.framework.bizaudit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 业务审计处理器
 */
public interface BizAuditHandler
{
    default Object captureBefore(BizAuditContext context) throws Exception
    {
        return null;
    }

    default Object captureAfter(BizAuditContext context, Object methodResult) throws Exception
    {
        return null;
    }

    default BizAuditOpType resolveOpType(BizAuditContext context, Object before, Object after)
    {
        return context.getAnnotation().opType();
    }

    default String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        return null;
    }

    default String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        return context.getAnnotation().bizName();
    }

    default String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        return null;
    }

    default Integer resolveBatchFlag(BizAuditContext context, Object before, Object after)
    {
        return 0;
    }

    default Integer resolveBatchCount(BizAuditContext context, Object before, Object after)
    {
        return null;
    }

    default Map<String, String> fieldLabels(BizAuditContext context)
    {
        return Collections.emptyMap();
    }

    default Map<String, String> fieldDictTypes(BizAuditContext context)
    {
        return Collections.emptyMap();
    }

    default Set<String> sensitiveFields(BizAuditContext context)
    {
        return Collections.emptySet();
    }

    /**
     * 需要在 diff 中完全忽略的字段名（不参与比较和展示）。
     * 默认忽略常见审计字段，业务可按需追加。
     */
    default Set<String> ignoredFields(BizAuditContext context)
    {
        return Collections.emptySet();
    }

    /**
     * 字段在 diff 中的显示顺序。
     * 返回的 List 决定 fieldDiffs 输出顺序；未在列表中的字段追加在末尾。
     * 默认为空（依赖 fieldLabels 插入顺序）。
     */
    default java.util.List<String> fieldOrder(BizAuditContext context)
    {
        return Collections.emptyList();
    }

    default Map<String, String> arrayKeyFields(BizAuditContext context)
    {
        return Collections.emptyMap();
    }

    default Map<String, String> arrayItemNameFields(BizAuditContext context)
    {
        return Collections.emptyMap();
    }

    default JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        return new JSONObject();
    }
}

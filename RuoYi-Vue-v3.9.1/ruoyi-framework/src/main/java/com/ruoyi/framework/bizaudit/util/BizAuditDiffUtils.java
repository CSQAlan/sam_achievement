package com.ruoyi.framework.bizaudit.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import com.ruoyi.framework.bizaudit.model.BizAuditDiffResult;
import com.ruoyi.system.domain.BizAuditLogDetail;
import com.ruoyi.system.service.ISysDictTypeService;

import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import com.ruoyi.common.utils.spring.SpringUtils;

/**
 * 审计 diff 工具
 * <p>
 * 优化点：
 * 1. 内置全局忽略字段（updateTime/createTime/delFlag 等技术字段）
 * 2. 支持 Handler 自定义忽略字段（ignoredFields）
 * 3. 字段显示顺序按 fieldLabels 定义顺序排列，未配置中文标签的字段默认跳过，避免乱码展示
 * 4. 支持 Handler 自定义排序（fieldOrder）
 * 5. 字典翻译缓存：同类型只查一次
 */
public final class BizAuditDiffUtils
{
    private BizAuditDiffUtils()
    {
    }

    /**
     * 全局内置忽略字段 —— 这些纯技术/审计字段不应出现在人可读的 diff 里
     */
    private static final Set<String> GLOBAL_IGNORED_FIELDS;

    static
    {
        Set<String> s = new HashSet<String>();
        s.add("createTime");
        s.add("updateTime");
        s.add("createBy");
        s.add("updateBy");
        s.add("delFlag");
        s.add("searchValue");
        s.add("params");
        s.add("remark");        // 若业务自己明确列出则再加
        GLOBAL_IGNORED_FIELDS = Collections.unmodifiableSet(s);
    }

    public static BizAuditDiffResult buildDiff(Object beforeObj, Object afterObj, BizAuditHandler handler, BizAuditContext context)
    {
        JSONObject before = toJsonObject(beforeObj);
        JSONObject after  = toJsonObject(afterObj);

        Map<String, String> fieldLabels   = handler.fieldLabels(context);
        Map<String, String> dictTypes     = handler.fieldDictTypes(context);
        Set<String> sensitiveFields       = new HashSet<String>(handler.sensitiveFields(context));
        Set<String> handlerIgnored        = handler.ignoredFields(context);
        List<String> fieldOrder           = handler.fieldOrder(context);
        Map<String, String> arrayKeyFields  = handler.arrayKeyFields(context);
        Map<String, String> arrayNameFields = handler.arrayItemNameFields(context);

        // 合并：全局忽略 + Handler 自定义忽略
        Set<String> ignoredFields = new HashSet<String>(GLOBAL_IGNORED_FIELDS);
        if (handlerIgnored != null) ignoredFields.addAll(handlerIgnored);

        JSONObject diff       = new JSONObject();
        JSONArray fieldDiffs  = new JSONArray();
        JSONArray arrayDiffs  = new JSONArray();
        List<BizAuditLogDetail> details = new ArrayList<BizAuditLogDetail>();

        // 用 List 收集原始 fieldDiffs（顺序敏感），再按 fieldOrder 排序
        List<JSONObject> rawFieldDiffs = new ArrayList<JSONObject>();

        compareObjects(before, after, "", rawFieldDiffs, arrayDiffs, details,
                fieldLabels, dictTypes, sensitiveFields, ignoredFields, arrayKeyFields, arrayNameFields);

        // ---- 按 fieldOrder / fieldLabels 定义顺序输出 fieldDiffs ----
        sortFieldDiffs(rawFieldDiffs, fieldOrder, fieldLabels);
        fieldDiffs.addAll(rawFieldDiffs);

        JSONObject summary = new JSONObject();
        summary.put("fieldChangeCount",  fieldDiffs.size());
        summary.put("detailChangeCount", details.size());
        summary.put("hasDiff", fieldDiffs.size() > 0 || arrayDiffs.size() > 0);
        diff.put("summary",    summary);
        diff.put("fieldDiffs", fieldDiffs);
        diff.put("arrayDiffs", arrayDiffs);

        BizAuditDiffResult result = new BizAuditDiffResult();
        result.setDiffJson(JSON.toJSONString(diff));
        result.setDetails(details);
        return result;
    }

    public static String maskSnapshotJson(Object value, Set<String> sensitiveFields)
    {
        if (value == null) return null;
        Object json = JSON.toJSON(value);
        maskSnapshot(json, "", sensitiveFields);
        return JSON.toJSONString(json);
    }

    // ==================== 核心递归比较 ====================

    private static void compareObjects(
            JSONObject before, JSONObject after, String prefix,
            List<JSONObject> fieldDiffs, JSONArray arrayDiffs,
            List<BizAuditLogDetail> details,
            Map<String, String> fieldLabels, Map<String, String> dictTypes,
            Set<String> sensitiveFields, Set<String> ignoredFields,
            Map<String, String> arrayKeyFields, Map<String, String> arrayNameFields)
    {
        Set<String> keys = new LinkedHashSet<String>();
        if (before != null) keys.addAll(before.keySet());
        if (after  != null) keys.addAll(after.keySet());

        for (String key : keys)
        {
            // 1. 跳过全局/Handler 忽略字段
            if (isIgnored(key, ignoredFields)) continue;

            String path = StringUtils.isBlank(prefix) ? key : prefix + "." + key;
            Object beforeValue = before == null ? null : before.get(key);
            Object afterValue  = after  == null ? null : after.get(key);

            // 2. 处理已配置的数组字段
            if (isConfiguredArray(path, beforeValue, afterValue, arrayKeyFields))
            {
                String itemKey       = arrayKeyFields.get(path);
                String itemNameField = arrayNameFields.get(path);
                JSONObject arrayDiff = buildArrayDiff(path, beforeValue, afterValue,
                        itemKey, itemNameField, fieldLabels, dictTypes, sensitiveFields, ignoredFields);
                arrayDiffs.add(arrayDiff);
                details.addAll(buildArrayDetails(path, beforeValue, afterValue,
                        itemKey, itemNameField, fieldLabels, dictTypes, sensitiveFields, ignoredFields));
                continue;
            }

            // 3. 嵌套对象递归
            if (beforeValue instanceof JSONObject || afterValue instanceof JSONObject)
            {
                compareObjects(asJsonObject(beforeValue), asJsonObject(afterValue), path,
                        fieldDiffs, arrayDiffs, details,
                        fieldLabels, dictTypes, sensitiveFields, ignoredFields, arrayKeyFields, arrayNameFields);
                continue;
            }

            // 4. 未在 fieldLabels 中配置的叶子字段：跳过，避免技术字段干扰
            if (!isLabelConfigured(path, fieldLabels)) continue;

            // 5. 值相同则跳过
            if (ObjectUtils.equals(normalizeValue(beforeValue), normalizeValue(afterValue))) continue;

            fieldDiffs.add(buildFieldDiff(path, beforeValue, afterValue, fieldLabels, dictTypes, sensitiveFields));
        }
    }

    // ==================== 数组差异 ====================

    private static JSONObject buildArrayDiff(
            String path, Object beforeValue, Object afterValue,
            String itemKey, String itemNameField,
            Map<String, String> fieldLabels, Map<String, String> dictTypes,
            Set<String> sensitiveFields, Set<String> ignoredFields)
    {
        JSONObject result = new JSONObject();
        List<BizAuditLogDetail> detailList = buildArrayDetails(
                path, beforeValue, afterValue, itemKey, itemNameField,
                fieldLabels, dictTypes, sensitiveFields, ignoredFields);

        JSONArray items = new JSONArray();
        for (BizAuditLogDetail detail : detailList)
        {
            JSONObject item = new JSONObject();
            item.put("itemPath",   detail.getItemPath());
            item.put("itemKey",    detail.getItemKey());
            item.put("itemBizId",  detail.getItemBizId());
            item.put("itemName",   detail.getItemName());
            item.put("changeType", detail.getChangeType());
            item.put("success",    detail.getSuccess());
            item.put("diff",       parseJson(detail.getDiffJson()));
            items.add(item);
        }
        result.put("path",     path);
        result.put("label",    resolveFieldLabel(path, fieldLabels));
        result.put("itemKey",  itemKey);
        result.put("items",    items);
        return result;
    }

    private static List<BizAuditLogDetail> buildArrayDetails(
            String path, Object beforeValue, Object afterValue,
            String itemKey, String itemNameField,
            Map<String, String> fieldLabels, Map<String, String> dictTypes,
            Set<String> sensitiveFields, Set<String> ignoredFields)
    {
        JSONArray beforeArray = toJsonArray(beforeValue);
        JSONArray afterArray  = toJsonArray(afterValue);

        Map<String, JSONObject> beforeMap = indexArray(beforeArray, itemKey);
        Map<String, JSONObject> afterMap  = indexArray(afterArray,  itemKey);

        Set<String> allKeys = new LinkedHashSet<String>();
        allKeys.addAll(beforeMap.keySet());
        allKeys.addAll(afterMap.keySet());

        List<String> sortedKeys = new ArrayList<String>(allKeys);
        Collections.sort(sortedKeys, Comparator.nullsLast(String::compareTo));

        List<BizAuditLogDetail> details = new ArrayList<BizAuditLogDetail>();
        int seq = 1;
        for (String key : sortedKeys)
        {
            JSONObject beforeItem = beforeMap.get(key);
            JSONObject afterItem  = afterMap.get(key);
            String changeType;
            if (beforeItem == null)
            {
                changeType = "ADDED";
            }
            else if (afterItem == null)
            {
                changeType = "REMOVED";
            }
            else if (JSON.toJSONString(beforeItem).equals(JSON.toJSONString(afterItem)))
            {
                continue;
            }
            else
            {
                changeType = "MODIFIED";
            }

            // 子对象字段级别 diff（同样走忽略逻辑）
            List<JSONObject> innerFieldDiffs = new ArrayList<JSONObject>();
            compareObjects(beforeItem, afterItem, path + "[" + key + "]",
                    innerFieldDiffs, new JSONArray(), new ArrayList<BizAuditLogDetail>(),
                    fieldLabels, dictTypes, sensitiveFields, ignoredFields,
                    Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap());

            JSONObject itemDiff = new JSONObject();
            itemDiff.put("changeType",  changeType);
            itemDiff.put("fieldDiffs",  innerFieldDiffs);

            BizAuditLogDetail detail = new BizAuditLogDetail();
            detail.setItemPath(path);
            detail.setItemKey(itemKey + "=" + key);
            detail.setItemBizId(key);
            detail.setItemName(resolveItemName(beforeItem, afterItem, itemNameField, key));
            detail.setSeqNo(seq++);
            detail.setChangeType(changeType);
            detail.setBeforeJson(beforeItem == null ? null : maskSnapshotJson(beforeItem, sensitiveFields));
            detail.setAfterJson(afterItem   == null ? null : maskSnapshotJson(afterItem,  sensitiveFields));
            detail.setDiffJson(JSON.toJSONString(itemDiff));
            detail.setSuccess(1);
            details.add(detail);
        }
        return details;
    }

    // ==================== 字段 diff 节点 ====================

    private static JSONObject buildFieldDiff(
            String path, Object beforeValue, Object afterValue,
            Map<String, String> fieldLabels, Map<String, String> dictTypes,
            Set<String> sensitiveFields)
    {
        JSONObject diff = new JSONObject();
        String dictType = resolveDictType(path, dictTypes);
        boolean masked  = isSensitive(path, sensitiveFields);

        String beforeText = translateDict(dictType, beforeValue);
        String afterText  = translateDict(dictType, afterValue);

        diff.put("path",       path);
        diff.put("field",      extractLeaf(path));
        diff.put("fieldLabel", resolveFieldLabel(path, fieldLabels));
        diff.put("changeType", beforeValue == null ? "ADDED" : afterValue == null ? "REMOVED" : "UPDATED");
        diff.put("before",     toCompatibleJsonValue(maskValue(path, beforeValue, sensitiveFields)));
        diff.put("after",      toCompatibleJsonValue(maskValue(path, afterValue,  sensitiveFields)));
        // beforeText/afterText：有字典翻译用翻译，无字典翻译用原始值字符串（保证可读性）
        diff.put("beforeText", beforeText != null ? beforeText : toReadableText(beforeValue));
        diff.put("afterText",  afterText  != null ? afterText  : toReadableText(afterValue));
        diff.put("masked",     masked);
        return diff;
    }

    // ==================== 工具：排序 ====================

    /**
     * 按 fieldOrder > fieldLabels 插入顺序 排序 fieldDiffs，未在顺序表中的放末尾。
     */
    private static void sortFieldDiffs(List<JSONObject> diffs, List<String> fieldOrder, Map<String, String> fieldLabels)
    {
        // 构建排序权重表：先用 fieldOrder，不够用则用 fieldLabels 的迭代顺序
        final Map<String, Integer> rankMap = new LinkedHashMap<String, Integer>();
        int rank = 0;
        for (String key : fieldOrder)
        {
            rankMap.put(key, rank++);
        }
        if (fieldLabels != null)
        {
            for (String key : fieldLabels.keySet())
            {
                if (!rankMap.containsKey(key)) rankMap.put(key, rank++);
            }
        }

        diffs.sort((a, b) -> {
            String fieldA = a.getString("field");
            String fieldB = b.getString("field");
            int rankA = rankMap.getOrDefault(fieldA, Integer.MAX_VALUE);
            int rankB = rankMap.getOrDefault(fieldB, Integer.MAX_VALUE);
            return Integer.compare(rankA, rankB);
        });
    }

    // ==================== 工具：判断 ====================

    private static boolean isIgnored(String key, Set<String> ignoredFields)
    {
        return ignoredFields != null && ignoredFields.contains(key);
    }

    /**
     * 判断字段（叶子 key 或完整 path）是否在 fieldLabels 中有中文配置。
     * 未配置的字段不展示在 diff 里，防止技术字段污染。
     */
    private static boolean isLabelConfigured(String path, Map<String, String> fieldLabels)
    {
        if (fieldLabels == null || fieldLabels.isEmpty()) return false;
        if (fieldLabels.containsKey(path)) return true;
        String leaf = extractLeaf(path);
        return fieldLabels.containsKey(leaf);
    }

    private static boolean isConfiguredArray(String path, Object beforeValue, Object afterValue,
            Map<String, String> arrayKeyFields)
    {
        return arrayKeyFields.containsKey(path) && (isArrayLike(beforeValue) || isArrayLike(afterValue));
    }

    private static boolean isArrayLike(Object value)
    {
        return value instanceof JSONArray || value instanceof Collection || (value != null && value.getClass().isArray());
    }

    private static JSONArray toJsonArray(Object value)
    {
        if (value == null) return new JSONArray();
        if (value instanceof JSONArray) return (JSONArray) value;
        try {
            Object parsed = JSON.toJSON(value);
            return parsed instanceof JSONArray ? (JSONArray) parsed : new JSONArray();
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    // ==================== 工具：字典/标签 ====================

    private static String resolveFieldLabel(String path, Map<String, String> fieldLabels)
    {
        if (fieldLabels.containsKey(path)) return fieldLabels.get(path);
        String leaf = extractLeaf(path);
        return fieldLabels.containsKey(leaf) ? fieldLabels.get(leaf) : leaf;
    }

    private static String resolveDictType(String path, Map<String, String> dictTypes)
    {
        if (dictTypes.containsKey(path)) return dictTypes.get(path);
        String leaf = extractLeaf(path);
        return dictTypes.get(leaf);
    }

    private static String translateDict(String dictType, Object value)
    {
        if (StringUtils.isBlank(dictType) || value == null) return null;
        ensureDictLoaded(dictType);
        return DictUtils.getDictLabel(dictType, toDictValue(value));
    }

    private static String toReadableText(Object value)
    {
        if (value == null) return "（空）";
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? "（空）" : s;
    }

    private static String toDictValue(Object value)
    {
        if (value instanceof Boolean) return ((Boolean) value) ? "1" : "0";
        return String.valueOf(value);
    }

    private static void ensureDictLoaded(String dictType)
    {
        List<SysDictData> cached = DictUtils.getDictCache(dictType);
        if (cached == null || cached.isEmpty())
        {
            SpringUtils.getBean(ISysDictTypeService.class).selectDictDataByType(dictType);
        }
    }

    // ==================== 工具：数组索引 ====================

    private static Map<String, JSONObject> indexArray(JSONArray array, String itemKey)
    {
        Map<String, JSONObject> result = new LinkedHashMap<String, JSONObject>();
        if (array == null) return result;
        int seq = 0;
        for (Object item : array)
        {
            JSONObject jsonObject = asJsonObject(item);
            String key = jsonObject == null ? String.valueOf(seq++) : jsonObject.getString(itemKey);
            if (StringUtils.isBlank(key)) key = String.valueOf(seq++);
            result.put(key, jsonObject);
        }
        return result;
    }

    private static String resolveItemName(JSONObject beforeItem, JSONObject afterItem,
            String itemNameField, String key)
    {
        if (StringUtils.isNotBlank(itemNameField))
        {
            JSONObject source = afterItem != null ? afterItem : beforeItem;
            if (source != null && StringUtils.isNotBlank(source.getString(itemNameField)))
            {
                return source.getString(itemNameField);
            }
        }
        return key;
    }

    // ==================== 工具：字段路径操作 ====================

    private static String extractLeaf(String path)
    {
        if (StringUtils.isBlank(path)) return path;
        String cleanPath = path;
        int lastDot = cleanPath.lastIndexOf('.');
        if (lastDot >= 0) cleanPath = cleanPath.substring(lastDot + 1);
        int bracket = cleanPath.lastIndexOf(']');
        if (bracket >= 0 && bracket + 1 < cleanPath.length())
        {
            cleanPath = cleanPath.substring(bracket + 1);
            if (cleanPath.startsWith(".")) cleanPath = cleanPath.substring(1);
        }
        return cleanPath;
    }

    // ==================== 工具：脱敏 ====================

    private static boolean isSensitive(String path, Set<String> sensitiveFields)
    {
        return sensitiveFields.contains(path) || sensitiveFields.contains(extractLeaf(path));
    }

    private static Object maskValue(String path, Object value, Set<String> sensitiveFields)
    {
        if (!isSensitive(path, sensitiveFields)) return value;
        String text = value == null ? null : String.valueOf(value);
        if (StringUtils.isBlank(text)) return text;
        if (text.length() <= 2) return "**";
        if (text.length() <= 6) return text.charAt(0) + "***" + text.charAt(text.length() - 1);
        return text.substring(0, 2) + "****" + text.substring(text.length() - 2);
    }

    private static void maskSnapshot(Object node, String path, Set<String> sensitiveFields)
    {
        if (node instanceof JSONObject)
        {
            JSONObject object = (JSONObject) node;
            for (String key : new ArrayList<String>(object.keySet()))
            {
                String currentPath = StringUtils.isBlank(path) ? key : path + "." + key;
                Object value = object.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray)
                {
                    maskSnapshot(value, currentPath, sensitiveFields);
                }
                else if (isSensitive(currentPath, sensitiveFields))
                {
                    object.put(key, maskValue(currentPath, value, sensitiveFields));
                }
            }
        }
        else if (node instanceof JSONArray)
        {
            JSONArray array = (JSONArray) node;
            for (int i = 0; i < array.size(); i++)
            {
                Object item = array.get(i);
                String currentPath = path + "[" + i + "]";
                if (item instanceof JSONObject || item instanceof JSONArray)
                {
                    maskSnapshot(item, currentPath, sensitiveFields);
                }
            }
        }
    }

    // ==================== 工具：类型转换 ====================

    private static JSONObject toJsonObject(Object value)
    {
        return asJsonObject(JSON.toJSON(value));
    }

    private static JSONObject asJsonObject(Object value)
    {
        if (value instanceof JSONObject) return (JSONObject) value;
        if (value == null) return null;
        Object json = JSON.toJSON(value);
        return json instanceof JSONObject ? (JSONObject) json : null;
    }

    private static Object normalizeValue(Object value)
    {
        if (value instanceof Boolean) return ((Boolean) value) ? 1 : 0;
        if (value instanceof Number || value instanceof String) return value;
        return value == null ? null : JSON.toJSONString(value);
    }

    private static Object toCompatibleJsonValue(Object value)
    {
        if (value instanceof Boolean) return ((Boolean) value) ? 1 : 0;
        return value;
    }

    private static Object parseJson(String text)
    {
        if (StringUtils.isBlank(text)) return null;
        return JSON.parse(text);
    }
}

-- 赛事/届次状态字典：sys_competition_status
-- 新增：2=预录（仅“赛事届次”使用；赛事主表仍只用0/1）
-- 脚本可重复执行：已存在则跳过

-- 1) 字典类型（若已存在则跳过）
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '赛事状态', 'sys_competition_status', '0', 'admin', NOW(), '赛事/届次状态'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_type WHERE dict_type = 'sys_competition_status'
);

-- 2) 字典数据：预录
INSERT INTO sys_dict_data (
  dict_sort, dict_label, dict_value, dict_type,
  css_class, list_class, is_default, status,
  create_by, create_time, remark
)
SELECT 3, '预录', '2', 'sys_competition_status', NULL, 'warning', 'N', '0', 'admin', NOW(), '届次预录（批量启用后可使用）'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_competition_status' AND dict_value = '2'
);


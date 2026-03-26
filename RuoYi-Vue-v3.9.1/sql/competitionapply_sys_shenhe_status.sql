-- 赛事申请：审核状态字典（sys_shenhe_status）
-- 说明：
-- 0=待审核，1=通过，2=驳回
-- 脚本可重复执行：已存在则跳过

-- 1) 字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '审核状态', 'sys_shenhe_status', '0', 'admin', NOW(), '赛事申请审核状态'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_type WHERE dict_type = 'sys_shenhe_status'
);

-- 2) 字典数据
INSERT INTO sys_dict_data (
  dict_sort, dict_label, dict_value, dict_type,
  css_class, list_class, is_default, status,
  create_by, create_time, remark
)
SELECT 1, '待审核', '0', 'sys_shenhe_status', NULL, 'primary', 'N', '0', 'admin', NOW(), '待审核'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_shenhe_status' AND dict_value = '0'
);

INSERT INTO sys_dict_data (
  dict_sort, dict_label, dict_value, dict_type,
  css_class, list_class, is_default, status,
  create_by, create_time, remark
)
SELECT 2, '通过', '1', 'sys_shenhe_status', NULL, 'success', 'N', '0', 'admin', NOW(), '审核通过'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_shenhe_status' AND dict_value = '1'
);

INSERT INTO sys_dict_data (
  dict_sort, dict_label, dict_value, dict_type,
  css_class, list_class, is_default, status,
  create_by, create_time, remark
)
SELECT 3, '驳回', '2', 'sys_shenhe_status', NULL, 'danger', 'N', '0', 'admin', NOW(), '审核驳回'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_shenhe_status' AND dict_value = '2'
);


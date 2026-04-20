-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情', '2100', '1', 'Reimbursement', 'system/Reimbursement/index', 1, 0, 'C', '0', '0', 'system:Reimbursement:list', '#', 'admin', sysdate(), '', null, '报销项目详情菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:Reimbursement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:Reimbursement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:Reimbursement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:Reimbursement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报销项目详情导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:Reimbursement:export',       '#', 'admin', sysdate(), '', null, '');
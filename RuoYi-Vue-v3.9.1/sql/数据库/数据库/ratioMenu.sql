-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio', '2220', '1', 'ratio', 'reimbursement_ratio/ratio/index', 1, 0, 'C', '0', '0', 'reimbursement_ratio:ratio:list', '#', 'admin', sysdate(), '', null, 'reimbursement_ratio菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'reimbursement_ratio:ratio:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'reimbursement_ratio:ratio:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'reimbursement_ratio:ratio:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'reimbursement_ratio:ratio:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('reimbursement_ratio导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'reimbursement_ratio:ratio:export',       '#', 'admin', sysdate(), '', null, '');
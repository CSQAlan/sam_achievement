-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果', '2000', '1', 'Sdept_unapproved', 'erp/Sdept_unapproved/index', 1, 0, 'C', '0', '0', 'erp:Sdept_unapproved:list', '#', 'admin', sysdate(), '', null, '校级未审核的成果菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'erp:Sdept_unapproved:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'erp:Sdept_unapproved:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'erp:Sdept_unapproved:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'erp:Sdept_unapproved:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('校级未审核的成果导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'erp:Sdept_unapproved:export',       '#', 'admin', sysdate(), '', null, '');
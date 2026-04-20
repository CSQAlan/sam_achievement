-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核', '2000', '1', 'college_level_reviewed', 'achievement/college_level_reviewed/index', 1, 0, 'C', '0', '0', 'achievement:college_level_reviewed:list', '#', 'admin', sysdate(), '', null, '院级已审核菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_reviewed:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_reviewed:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_reviewed:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_reviewed:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级已审核导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_reviewed:export',       '#', 'admin', sysdate(), '', null, '');
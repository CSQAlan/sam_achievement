-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核', '2000', '1', 'college_level_unreviewed', 'achievement/college_level_unreviewed/index', 1, 0, 'C', '0', '0', 'achievement:college_level_unreviewed:list', '#', 'admin', sysdate(), '', null, '院级未审核菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_unreviewed:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_unreviewed:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_unreviewed:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_unreviewed:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('院级未审核导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'achievement:college_level_unreviewed:export',       '#', 'admin', sysdate(), '', null, '');
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心', '0', '1', 'user', 'system/user/index', 1, 0, 'C', '0', '0', 'system:user:list', '#', 'admin', sysdate(), '', null, '用户中心菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:user:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:user:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:user:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:user:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户中心导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:user:export',       '#', 'admin', sysdate(), '', null, '');


UPDATE sys_menu SET component = 'system/user/index' WHERE perms = 'system:user:list';
UPDATE sys_menu SET perms = 'system:user:list' WHERE perms = 'achievement:user:list';

SELECT @pid := menu_id FROM sys_menu WHERE perms = 'system:user:list' LIMIT 1;

UPDATE sys_menu SET perms = 'system:user:query'  WHERE parent_id = @pid AND perms = 'achievement:user:query';
UPDATE sys_menu SET perms = 'system:user:add'    WHERE parent_id = @pid AND perms = 'achievement:user:add';
UPDATE sys_menu SET perms = 'system:user:edit'   WHERE parent_id = @pid AND perms = 'achievement:user:edit';
UPDATE sys_menu SET perms = 'system:user:remove' WHERE parent_id = @pid AND perms = 'achievement:user:remove';
UPDATE sys_menu SET perms = 'system:user:export' WHERE parent_id = @pid AND perms = 'achievement:user:export';



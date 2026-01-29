-- 审阅页面菜单（隐藏）
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成果审阅', '2000', '99', 'review', 'erp/review', 1, 0, 'C', '1', '0', '', '#', 'admin', sysdate(), '', null, '成果审阅页面（隐藏）');

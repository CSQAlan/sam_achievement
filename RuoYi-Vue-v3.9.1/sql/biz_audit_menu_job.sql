-- 业务审计菜单，父级默认挂在“系统监控”(menu_id=108) 下，请按实际库调整 parent_id
INSERT INTO sys_menu VALUES (10850, '业务审计', 108, 8, 'biz-audit', 'monitor/bizAudit/index', NULL, '', 1, 0, 'C', '0', '0', 'monitor:bizaudit:list', 'clipboard', 'admin', NOW(), '', NULL, '高级业务审计日志');
INSERT INTO sys_menu VALUES (10851, '业务审计查询', 10850, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'monitor:bizaudit:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (10852, '业务审计删除', 10850, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'monitor:bizaudit:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (10853, '业务审计导出', 10850, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'monitor:bizaudit:export', '#', 'admin', NOW(), '', NULL, '');

-- Quartz 清理任务，每天凌晨 3 点清理 30 天前业务审计日志
INSERT INTO sys_job VALUES
(10850, '业务审计日志清理', 'DEFAULT', 'bizAuditCleanTask.clean30Days()', '0 0 3 * * ?', '3', '1', '0', 'admin', NOW(), '', NULL, '每天凌晨清理30天前的业务审计日志');

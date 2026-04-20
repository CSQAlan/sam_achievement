-- ----------------------------
-- 报销项目表
-- ----------------------------
DROP TABLE IF EXISTS `sam_reimbursement_items`;
CREATE TABLE `sam_reimbursement_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '报销项目名称',
  `reimbursement_time` date NOT NULL COMMENT '报销时间',
  `total_fee` double NOT NULL COMMENT '总金额',
  `paid_fee` double DEFAULT NULL COMMENT '已发放金额',
  `amount` int(11) DEFAULT NULL COMMENT '报销项目数量',
  `owner_dep_id` bigint(20) NOT NULL COMMENT '归属学院（sys_dept.dept_id顶层学院）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0进行中 1已完成）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记（0存在 2删除）',
  PRIMARY KEY (`id`),
  KEY `idx_owner_dep_id` (`owner_dep_id`),
  KEY `idx_status` (`status`),
  KEY `idx_reimbursement_time` (`reimbursement_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销项目表';

-- ----------------------------
-- 素质提升表
-- ----------------------------
DROP TABLE IF EXISTS `sam_quality_development_form`;
CREATE TABLE `sam_quality_development_form` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `year` int(11) NOT NULL COMMENT '年份',
  `amount` int(11) NOT NULL COMMENT '统计成果数',
  `owner_dep_id` bigint(20) DEFAULT NULL COMMENT '归属学院（sys_dept.dept_id顶层学院）',
  `create_time` datetime DEFAULT NULL COMMENT '生成时间',
  PRIMARY KEY (`id`),
  KEY `idx_year` (`year`),
  KEY `idx_owner_dep_id` (`owner_dep_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素质提升表';

-- ----------------------------
-- 报销比例表
-- ----------------------------
DROP TABLE IF EXISTS `sam_reimbursement_ratio`;
CREATE TABLE `sam_reimbursement_ratio` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `grade` varchar(50) NOT NULL COMMENT '获奖等级（一等奖、二等奖、三等奖）',
  `category` varchar(50) DEFAULT NULL COMMENT '类别（政府类、学会类）',
  `ratio` int(11) DEFAULT NULL COMMENT '报销百分比',
  `owner_dep_id` bigint(20) DEFAULT NULL COMMENT '归属学院（sys_dept.dept_id；为空表示全校规则）',
  `status` char(1) DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记（0存在 2删除）',
  PRIMARY KEY (`id`),
  KEY `idx_grade` (`grade`),
  KEY `idx_category` (`category`),
  KEY `idx_owner_dep_id` (`owner_dep_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销比例表';

-- ----------------------------
-- 字典数据 - 获奖等级
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (NULL, '获奖等级', 'award_grade', '0', 'admin', NOW(), '', NULL, '获奖等级字典');

INSERT INTO `sys_dict_data` VALUES (NULL, 1, '一等奖', '1', 'award_grade', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '一等奖');
INSERT INTO `sys_dict_data` VALUES (NULL, 2, '二等奖', '2', 'award_grade', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '二等奖');
INSERT INTO `sys_dict_data` VALUES (NULL, 3, '三等奖', '3', 'award_grade', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '三等奖');

-- ----------------------------
-- 字典数据 - 类别
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (NULL, '报销类别', 'reimbursement_category', '0', 'admin', NOW(), '', NULL, '报销类别字典');

INSERT INTO `sys_dict_data` VALUES (NULL, 1, '政府类', '1', 'reimbursement_category', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '政府类');
INSERT INTO `sys_dict_data` VALUES (NULL, 2, '学会类', '2', 'reimbursement_category', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '学会类');

-- ----------------------------
-- 字典数据 - 报销项目状态
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (NULL, '报销项目状态', 'reimbursement_status', '0', 'admin', NOW(), '', NULL, '报销项目状态字典');

INSERT INTO `sys_dict_data` VALUES (NULL, 1, '进行中', '0', 'reimbursement_status', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '进行中');
INSERT INTO `sys_dict_data` VALUES (NULL, 2, '已完成', '1', 'reimbursement_status', '', 'success', 'Y', '0', 'admin', NOW(), '', NULL, '已完成');

-- ----------------------------
-- 字典数据 - 报销比例状态
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (NULL, '报销比例状态', 'ratio_status', '0', 'admin', NOW(), '', NULL, '报销比例状态字典');

INSERT INTO `sys_dict_data` VALUES (NULL, 1, '停用', '0', 'ratio_status', '', 'danger', 'Y', '0', 'admin', NOW(), '', NULL, '停用');
INSERT INTO `sys_dict_data` VALUES (NULL, 2, '启用', '1', 'ratio_status', '', 'success', 'Y', '0', 'admin', NOW(), '', NULL, '启用');


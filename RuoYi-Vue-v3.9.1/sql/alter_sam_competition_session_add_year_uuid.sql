-- 为赛事届次表增加 year / uuid（参赛通知附件uuid）
-- 说明：
-- - year：默认当前年由后端填充；这里允许为NULL，避免历史环境迁移失败
-- - uuid：参赛通知附件uuid（sys_file_uuid.file_uuid），允许为NULL，后端会校验必填
-- 可重复执行：已存在列则跳过

-- year
SET @year_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sam_competition_session'
    AND COLUMN_NAME = 'year'
);
SET @sql_year := IF(@year_exists = 0,
  'ALTER TABLE sam_competition_session ADD COLUMN year INT NULL COMMENT ''年份'' AFTER session',
  'SELECT ''sam_competition_session.year already exists''');
PREPARE stmt FROM @sql_year;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- uuid
SET @uuid_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sam_competition_session'
    AND COLUMN_NAME = 'uuid'
);
SET @sql_uuid := IF(@uuid_exists = 0,
  'ALTER TABLE sam_competition_session ADD COLUMN uuid VARCHAR(64) NULL COMMENT ''参赛通知附件UUID'' AFTER year',
  'SELECT ''sam_competition_session.uuid already exists''');
PREPARE stmt2 FROM @sql_uuid;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;


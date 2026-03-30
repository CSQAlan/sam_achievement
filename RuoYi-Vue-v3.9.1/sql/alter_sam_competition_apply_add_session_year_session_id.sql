-- 为赛事申请表增加届次信息（session/year）及审核生成的届次ID（session_id）
-- 可重复执行：已存在列则跳过

-- session（届次文本）
SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sam_competition_apply'
    AND COLUMN_NAME = 'session'
);
SET @sql1 := IF(@col_exists = 0,
  'ALTER TABLE sam_competition_apply ADD COLUMN session VARCHAR(50) NULL COMMENT ''届次（例如2025、十二届）'' AFTER name',
  'SELECT ''sam_competition_apply.session already exists''');
PREPARE stmt FROM @sql1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- year（年份）
SET @col_exists2 := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sam_competition_apply'
    AND COLUMN_NAME = 'year'
);
SET @sql2 := IF(@col_exists2 = 0,
  'ALTER TABLE sam_competition_apply ADD COLUMN year INT NULL COMMENT ''年份'' AFTER session',
  'SELECT ''sam_competition_apply.year already exists''');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- session_id（审核通过后生成的届次ID）
SET @col_exists3 := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sam_competition_apply'
    AND COLUMN_NAME = 'session_id'
);
SET @sql3 := IF(@col_exists3 = 0,
  'ALTER TABLE sam_competition_apply ADD COLUMN session_id BIGINT NULL COMMENT ''生成届次ID（sam_competition_session.session_id）'' AFTER competition_id',
  'SELECT ''sam_competition_apply.session_id already exists''');
PREPARE stmt3 FROM @sql3;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;


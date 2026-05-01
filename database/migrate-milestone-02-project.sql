-- Milestone 02 non-destructive migration.
-- Adds project workspaces and binds old user stories / sprints to a default project.

USE scrumcat;

CREATE TABLE IF NOT EXISTS project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  owner_nickname VARCHAR(50) DEFAULT NULL,
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_project_creator (creator_id)
);

CREATE TABLE IF NOT EXISTS collaboration_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  target_type VARCHAR(30) NOT NULL,
  target_id BIGINT NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'MEMBER',
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_member_target (target_type, target_id),
  INDEX idx_member_creator (creator_id)
);

SET @schema_name = DATABASE();

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE user_story ADD COLUMN project_id BIGINT NULL',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'user_story'
    AND column_name = 'project_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE user_story ADD COLUMN owner_nickname VARCHAR(50) DEFAULT NULL',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'user_story'
    AND column_name = 'owner_nickname'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE sprint ADD COLUMN project_id BIGINT NULL',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'sprint'
    AND column_name = 'project_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE sprint ADD COLUMN owner_nickname VARCHAR(50) DEFAULT NULL',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'sprint'
    AND column_name = 'owner_nickname'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO project (name, description, owner_nickname, creator_id)
SELECT 'ScrumCat 默认项目', '由 Milestone 02 迁移脚本为旧数据创建', u.nickname, u.id
FROM sys_user u
WHERE EXISTS (SELECT 1 FROM user_story s WHERE s.creator_id = u.id)
  AND NOT EXISTS (SELECT 1 FROM project p WHERE p.creator_id = u.id);

INSERT INTO project (name, description, owner_nickname, creator_id)
SELECT 'ScrumCat 默认项目', '由 Milestone 02 迁移脚本为旧数据创建', u.nickname, u.id
FROM sys_user u
WHERE EXISTS (SELECT 1 FROM sprint sp WHERE sp.creator_id = u.id)
  AND NOT EXISTS (SELECT 1 FROM project p WHERE p.creator_id = u.id);

UPDATE user_story s
JOIN (
  SELECT creator_id, MIN(id) AS project_id
  FROM project
  GROUP BY creator_id
) p ON p.creator_id = s.creator_id
SET s.project_id = p.project_id
WHERE s.project_id IS NULL;

UPDATE sprint sp
JOIN (
  SELECT creator_id, MIN(id) AS project_id
  FROM project
  GROUP BY creator_id
) p ON p.creator_id = sp.creator_id
SET sp.project_id = p.project_id
WHERE sp.project_id IS NULL;

ALTER TABLE user_story
  MODIFY COLUMN project_id BIGINT NOT NULL;

ALTER TABLE sprint
  MODIFY COLUMN project_id BIGINT NOT NULL;

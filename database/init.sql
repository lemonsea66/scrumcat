-- ScrumCat database initialization script
-- Database: scrumcat
-- Character set: utf8mb4
-- Collation: utf8mb4_unicode_ci

CREATE DATABASE IF NOT EXISTS scrumcat
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE scrumcat;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  avatar VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS user_story (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  story_point DECIMAL(4,1) NOT NULL,
  priority INT NOT NULL DEFAULT 999,
  status VARCHAR(30) NOT NULL DEFAULT 'TODO',
  owner_nickname VARCHAR(50) DEFAULT NULL,
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_story_project (project_id),
  INDEX idx_story_creator (creator_id),
  INDEX idx_story_status (status),
  INDEX idx_story_priority (priority)
);

CREATE TABLE IF NOT EXISTS sprint (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  goal VARCHAR(255) DEFAULT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
  owner_nickname VARCHAR(50) DEFAULT NULL,
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_sprint_project (project_id),
  INDEX idx_sprint_creator (creator_id),
  INDEX idx_sprint_status (status)
);

CREATE TABLE IF NOT EXISTS sprint_story (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sprint_id BIGINT NOT NULL,
  story_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sprint_story (sprint_id, story_id),
  INDEX idx_ss_sprint (sprint_id),
  INDEX idx_ss_story (story_id)
);

CREATE TABLE IF NOT EXISTS story_status_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sprint_id BIGINT NOT NULL,
  story_id BIGINT NOT NULL,
  old_status VARCHAR(30) DEFAULT NULL,
  new_status VARCHAR(30) NOT NULL,
  changed_by BIGINT NOT NULL,
  changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ssl_sprint (sprint_id),
  INDEX idx_ssl_story (story_id),
  INDEX idx_ssl_changed_at (changed_at)
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

CREATE TABLE IF NOT EXISTS task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  story_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  estimated_hours INT NOT NULL DEFAULT 1,
  status VARCHAR(30) NOT NULL DEFAULT 'TODO',
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_task_story (story_id),
  INDEX idx_task_status (status)
);

CREATE TABLE IF NOT EXISTS issue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  source VARCHAR(30) NOT NULL DEFAULT '开发过程',
  status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
  owner VARCHAR(50) DEFAULT NULL,
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_issue_status (status),
  INDEX idx_issue_creator (creator_id)
);

CREATE TABLE IF NOT EXISTS retrospective (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sprint_id BIGINT NOT NULL,
  good_1 VARCHAR(255) DEFAULT NULL,
  good_2 VARCHAR(255) DEFAULT NULL,
  good_3 VARCHAR(255) DEFAULT NULL,
  improve_1 VARCHAR(255) DEFAULT NULL,
  improve_2 VARCHAR(255) DEFAULT NULL,
  improve_3 VARCHAR(255) DEFAULT NULL,
  creator_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_retrospective_sprint (sprint_id)
);

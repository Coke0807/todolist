DROP DATABASE IF EXISTS todolist;
CREATE DATABASE todolist CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE todolist;

DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    completed TINYINT(1) NOT NULL DEFAULT 0,
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    due_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO tasks (title, description, completed, priority, due_date, created_at, updated_at) VALUES
('编写接口文档', '整理所有开放 API 的输入输出说明，便于前端联调。', 0, 'HIGH', DATE_ADD(CURDATE(), INTERVAL 2 DAY), NOW(), NOW()),
('整理 UI 规范', '拉齐配色、字体、阴影等视觉规范，方便统一体验。', 0, 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 5 DAY), NOW(), NOW()),
('撰写用户手册', '面向业务同学输出简洁的操作指引。', 0, 'LOW', DATE_ADD(CURDATE(), INTERVAL 7 DAY), NOW(), NOW()),
('回归测试', '验证新增主题切换功能在移动端表现。', 1, 'HIGH', DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW()),
('部署演练', '在本地模拟一次完整的部署流程，记录可能的坑位。', 0, 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 3 DAY), NOW(), NOW());

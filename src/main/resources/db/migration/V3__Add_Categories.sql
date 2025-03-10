-- 创建分类表（如果不存在）
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    sort INT DEFAULT 0,
    available BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入基础分类数据
INSERT INTO categories (name, description, sort) VALUES 
('热菜', '各种热炒菜品', 10),
('凉菜', '开胃爽口的凉拌菜', 20),
('主食', '米饭、面食等主食', 30),
('汤类', '各种营养美味的汤品', 40),
('小炒', '快速可口的小炒菜', 50),
('特色菜', '本店特色招牌菜品', 60),
('饮品', '各类饮料和汤品', 70),
('小吃', '可口的各式小吃', 80);

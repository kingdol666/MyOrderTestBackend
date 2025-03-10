-- 创建菜品数据
INSERT INTO menu_items (name, description, price, image_url, available, is_recommend, sales_count, category_id, create_time, update_time) VALUES 
-- 热菜类
('宫保鸡丁', '经典川菜，口感麻辣鲜香', 28.00, 'https://example.com/images/gongbao.jpg', true, true, 100, 
 (SELECT id FROM categories WHERE name = '热菜'), NOW(), NOW()),
 
('红烧肉', '肥而不腻，入口即化', 32.00, 'https://example.com/images/hongshaorou.jpg', true, true, 150,
 (SELECT id FROM categories WHERE name = '热菜'), NOW(), NOW()),

('麻婆豆腐', '麻辣鲜香，下饭首选', 22.00, 'https://example.com/images/mapo.jpg', true, false, 80,
 (SELECT id FROM categories WHERE name = '热菜'), NOW(), NOW()),

-- 凉菜类
('凉拌黄瓜', '清脆爽口，开胃解腻', 12.00, 'https://example.com/images/cucumber.jpg', true, false, 50,
 (SELECT id FROM categories WHERE name = '凉菜'), NOW(), NOW()),
 
('口水鸡', '麻辣鲜香，回味无穷', 26.00, 'https://example.com/images/koushuiji.jpg', true, true, 120,
 (SELECT id FROM categories WHERE name = '凉菜'), NOW(), NOW()),

-- 主食类
('扬州炒饭', '色香味俱全，经典美味', 16.00, 'https://example.com/images/yangzhou.jpg', true, true, 200,
 (SELECT id FROM categories WHERE name = '主食'), NOW(), NOW()),
 
('阳春面', '汤鲜面滑，经典好味', 12.00, 'https://example.com/images/yangchun.jpg', true, false, 90,
 (SELECT id FROM categories WHERE name = '主食'), NOW(), NOW()),

-- 汤类
('番茄蛋汤', '酸甜可口，营养美味', 15.00, 'https://example.com/images/tomato.jpg', true, false, 70,
 (SELECT id FROM categories WHERE name = '汤类'), NOW(), NOW()),
 
('紫菜蛋花汤', '清淡爽口，开胃解腻', 12.00, 'https://example.com/images/seaweed.jpg', true, false, 60,
 (SELECT id FROM categories WHERE name = '汤类'), NOW(), NOW()),

-- 特色菜
('铁板牛肉', '现煎现卖，香气四溢', 46.00, 'https://example.com/images/beef.jpg', true, true, 180,
 (SELECT id FROM categories WHERE name = '特色菜'), NOW(), NOW()),
 
('水煮鱼', '麻辣鲜香，鱼肉鲜嫩', 58.00, 'https://example.com/images/fish.jpg', true, true, 160,
 (SELECT id FROM categories WHERE name = '特色菜'), NOW(), NOW()),

-- 饮品类
('柠檬茶', '清爽解腻，回味甘甜', 8.00, 'https://example.com/images/lemon.jpg', true, false, 100,
 (SELECT id FROM categories WHERE name = '饮品'), NOW(), NOW()),
 
('奶茶', '香浓丝滑，甜而不腻', 10.00, 'https://example.com/images/milk-tea.jpg', true, true, 220,
 (SELECT id FROM categories WHERE name = '饮品'), NOW(), NOW()),

-- 小吃类
('蛋炒饭', '简单美味，老少皆宜', 12.00, 'https://example.com/images/egg-rice.jpg', true, false, 150,
 (SELECT id FROM categories WHERE name = '小吃'), NOW(), NOW()),
 
('炸鸡翅', '外酥内嫩，香气四溢', 16.00, 'https://example.com/images/chicken.jpg', true, true, 180,
 (SELECT id FROM categories WHERE name = '小吃'), NOW(), NOW()); 
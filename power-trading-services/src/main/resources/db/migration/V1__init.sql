-- 初始化数据脚本（可选）
-- 插入示例商品数据

INSERT INTO products (name, type, unit, description, status, create_time) VALUES
('峰时电力', 'PEAK', 'MWh', '用电高峰时段的电力', 'ACTIVE', NOW()),
('谷时电力', 'VALLEY', 'MWh', '用电低谷时段的电力', 'ACTIVE', NOW()),
('平时电力', 'FLAT', 'MWh', '正常时段的电力', 'ACTIVE', NOW());


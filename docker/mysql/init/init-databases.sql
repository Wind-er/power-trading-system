-- 初始化数据库
CREATE DATABASE IF NOT EXISTS `nacos_config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建业务数据库
CREATE DATABASE IF NOT EXISTS `power_trading_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `power_trading_product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `power_trading_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `power_trading_trade` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `power_trading_market` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `seata` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER IF NOT EXISTS 'power_trading'@'%' IDENTIFIED BY 'power_trading123';
GRANT ALL PRIVILEGES ON `power_trading_user`.* TO 'power_trading'@'%';
GRANT ALL PRIVILEGES ON `power_trading_product`.* TO 'power_trading'@'%';
GRANT ALL PRIVILEGES ON `power_trading_order`.* TO 'power_trading'@'%';
GRANT ALL PRIVILEGES ON `power_trading_trade`.* TO 'power_trading'@'%';
GRANT ALL PRIVILEGES ON `power_trading_market`.* TO 'power_trading'@'%';
GRANT ALL PRIVILEGES ON `seata`.* TO 'power_trading'@'%';
FLUSH PRIVILEGES;

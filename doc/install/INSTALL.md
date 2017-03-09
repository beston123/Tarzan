## Requirements

- Ubuntu/Debian/CentOS/RHEL**
- jdk 1.6+
- MySQL 5.0+
- redis 2.4+
- zookeeper 3.4+
- RocketMQ 3.2.0+

## Installation

### 数据库部署
- 按顺序执行所有.sql文件

### Server部署
#### 配置
- config.properties: 端口、serverId、权重、zookeeper、mq相关配置
- log4j.properties: 日志配置
- store.properties: 数据库、redis等配置

#### 启动

    sh startup.sh

#### 停止

    sh shutdown.sh

### Admin部署
#### 配置
- config.properties: zookeeper地址配置

### 启停
- 按web应用方式部署

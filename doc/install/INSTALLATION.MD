## Requirements

- Ubuntu/Debian/CentOS/RHEL**
- jdk 1.6+
- MySQL 5.0+
- redis 2.4+
- zookeeper 3.4+
- RocketMQ 3.2.0+

## Building

    ./deploy.sh
    
如果提示缺少的依赖‘baymax-spring’，请下载[baymax-spring_3.0.0.zip](https://github.com/beston123/Tarzan/tree/master/doc/install)，并解压到本地maven仓库。

BayMax: https://github.com/tongbanjie/baymax

## Installation

### 数据库部署
- 按顺序执行所有.sql文件

### 服务端部署
#### 配置
- config.properties: 端口、服务端Id、权重、zookeeper、MQ地址等配置
- log4j.properties: 日志配置
- store.properties: 数据库、redis等配置

#### 启动

    ./startup.sh

#### 停止

    ./shutdown.sh

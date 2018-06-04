### Requirements

- Ubuntu/Debian/CentOS/RHEL**
- jdk 1.6+
- MySQL 5.0+
- redis 2.4+
- zookeeper 3.4+
- RocketMQ 3.2.0+

### Building

    ./deploy.sh

构建产物：/target/tarzan-${version}.tar.gz

### Installation
#### 数据库部署  
- 创建数据库schema：tarzan
- 执行sql文件：doc/install/1_create_tables.sql

#### 服务端部署
##### 准备
上传程序包‘tarzan-${version}.tar.gz’到服务器，并解压

##### 配置  
- config.properties: 监听端口，数据中心Id，服务端权重，zookeeper地址，RocketMQ地址
- log4j.properties: 日志配置
- store.properties: 数据库，Redis配置

##### 启动  

    ./startup.sh

##### 停止  

    ./shutdown.sh

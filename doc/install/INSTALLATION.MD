### Requirements

- Ubuntu/Debian/CentOS/RHEL**
- jdk 1.6+
- MySQL 5.0+
- redis 2.4+
- zookeeper 3.4+
- RocketMQ 3.2.0+

### Building

    ./deploy.sh
    
如果提示缺少的依赖‘baymax-spring’，请下载[baymax-spring_3.0.0.zip](https://github.com/beston123/Tarzan/raw/master/doc/install/baymax-spring_3.0.0.zip)，并解压到本地maven仓库。

BayMax: https://github.com/tongbanjie/baymax

### Installation
#### 数据库部署  
- 创建数据库schema：tarzan
- 执行sql文件：doc/install/1_create_tables.sql

#### 服务端部署  
##### 配置  
- config.properties: 监听端口，服务端Id，服务端权重，zookeeper地址，RocketMQ地址
- log4j.properties: 日志配置
- store.properties: 数据库，Redis配置

##### 启动  

    ./startup.sh

##### 停止  

    ./shutdown.sh

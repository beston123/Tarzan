# 可靠事件系统（Tarzan）

## 简介
Tarzan取自电影《人猿泰山》的英文名，用于保证消息生产者的消息被可靠投递
- [admin](https://github.com/beston123/Tarzan-admin): 服务端和消息监控中心
- client: 客户端，服务端连接管理和事务消息发送
- cluster: 集群策略和负载均衡
- common: 公用的代码
- registry: 注册中心，提供注册和自动发现
- rpc: 远程通讯框架，基于netty
- server: 服务端，消息接受和代理发送，事务控制和事务状态回查
- store: 服务端的数据存储
- mq: 各类mq客户端，集成到应用

## 原理
分布式事务的三种实现方式，包括可靠事件模式、业务补偿模式、TCC模式。

可靠事件系统Tarzan是基于可靠事件模式的分布式事务解决方案。

参考：
- [分布式开放消息系统(RocketMQ)的原理与实践](http://www.jianshu.com/p/453c6e7ff81c)
- [微服务架构下的数据一致性：可靠事件模式](http://blog.csdn.net/liuxinghao/article/details/51924877)

## 设计

基于外部事件表，实现可靠的消息投递
![外部事件表](https://github.com/beston123/tevent/blob/master/doc/development/ExternalEventTable.png)

生产者事务消息实现原理
![生产者事务消息](https://github.com/beston123/tevent/blob/master/doc/development/TransactionMessage.png)

可靠消息系统实现架构图
![架构图](https://github.com/beston123/tevent/blob/master/doc/development/Architecture.png)

可靠消息系统Tarzan数据流图
![数据流图](https://github.com/beston123/tevent/blob/master/doc/development/DataFlow.png)

## 版本
[更新日志...](https://github.com/beston123/Tarzan/blob/master/CHANGELOG.md)

## 部署说明
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
- 创建数据库schema：workflow
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

## 建议
若您有任何建议，可以通过QQ群或邮件反馈。

QQ群：668250402

Email：beston@yeah.net

# 可靠事件系统（tevent）

铜板街可靠事件系统，用于保证消息生产者的可靠消息投递     
client: 客户端，集成到应用
common: 公用的代码
registry：注册中心，提供注册和自动发现
rpc：远程通讯框架，目前基于netty
server：服务端，消息接受和代理发送，事务控制
store：数据存储

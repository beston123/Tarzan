# 可靠事件系统（tevent）

## 简介
可靠事件系统，用于保证消息生产者的可靠消息投递
- admin: 监控中心
- client: 客户端，服务端连接管理和事务消息发送
- cluster: 集群策略和负载均衡
- common: 公用的代码
- registry: 注册中心，提供注册和自动发现
- rpc: 远程通讯框架，基于NIO
- server: 服务端，消息接受和代理发送，事务控制和事务状态回查
- store: 服务端的数据存储
- mq: 各类mq客户端，集成到应用

## 架构图
![架构图](https://github.com/beston123/tevent/blob/master/doc/Architecture.png)

## 生产者事务消息
![生产者事务消息](https://github.com/beston123/tevent/blob/master/doc/TransactionMessage.png)

## 版本更新
## [Unreleased]
### Added
- 新增Redis锁，避免定时任务并发

### Changed
-

## [0.3.1] - 2016-12-05
### Added
- 新增从消息分表汇总到待处理表，超时的消息汇总到［待事务检查表］，
发送失败的消息汇总到［待发送表］
- 新增消息重发定时任务，对发送失败的MQ消息定时重发
- 新增TEvent数据流图

### Changed
- 优化消息事务状态检查定时任务
- 更新TEvent架构图

## [0.3.0] - 2016-11-29
### Added
- 新增admin管理端，支持服务端和ServerId列表查询，
及失效ServerId的删除

## [0.2.0] - 2016-11-16
### Added
- 新增单元测试

### Changed
- 重构Store模块

## [0.1.0] - 2016-11-03
### Added
- 新增Server端启动脚本
- 新增tbjmq客户端

### Changed
- 重构Server和Client配置
- 重构Server端MQ消息发送者
- 重构MQ客户端

[Unreleased]: https://github.com/beston123/tevent
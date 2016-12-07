# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

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

[Unreleased]: http://git.tongbanjie.com/ware/tevent/tree/master
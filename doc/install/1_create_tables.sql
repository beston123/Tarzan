##################################################################
## Tarzan 数据库
##################################################################
CREATE DATABASE `tarzan` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

USE tarzan;

##################################################################
## Tarzan 系统表
##################################################################
CREATE TABLE tarzan.`tz_message_aggregate_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `time_start` datetime NOT NULL COMMENT '开始时间点',
  `time_end` datetime NOT NULL COMMENT '截至时间点',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `aggregate_type` tinyint(4) NOT NULL COMMENT '汇总类型',
  `status` tinyint(4) NOT NULL COMMENT '处理状态 0 初始 1成功 -1失败',
  `record_count` int(11) DEFAULT NULL COMMENT '处理记录数',
  `elapsed_time` bigint(20) DEFAULT NULL COMMENT '执行时间ms',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT ' 备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `PLAN_UNIQUE` (`time_start`,`mq_type`,`aggregate_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息汇总计划表';

CREATE TABLE tarzan.`tz_to_check_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tid` bigint(20) NOT NULL COMMENT '事务消息Id',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `source_time` datetime NOT NULL COMMENT '消息来源时间',
  `retry_count` smallint(8) NOT NULL COMMENT '事务检查次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `TID` (`tid`),
  KEY `SOURCE_TIME` (`source_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='待检查事务状态的消息表';

CREATE TABLE tarzan.`tz_to_send_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tid` bigint(20) NOT NULL COMMENT '事务消息Id',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `source_time` datetime NOT NULL COMMENT '消息来源时间',
  `retry_count` smallint(8) NOT NULL COMMENT '发送次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `TID` (`tid`),
  KEY `SOURCE_TIME` (`source_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='待发送的消息表';

CREATE TABLE tarzan.`tz_message_consume` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tid` bigint(20) DEFAULT NULL COMMENT '消息Tid',
  `message_id` varchar(45) NOT NULL COMMENT '消息Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `consumer_group` varchar(60) NOT NULL COMMENT '消费者group',
  `topic` varchar(60) NOT NULL COMMENT 'Topic',
  `consumer` varchar(45) NOT NULL COMMENT '消费者［ ip/appName］',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `consume_status` tinyint(1) NOT NULL COMMENT '消费状态',
  `reconsume_times` smallint(8) NOT NULL COMMENT '重新消费次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `TID_UNIQUE` (`tid`,`consumer_group`),
  KEY `MESSAGE_KEY` (`message_key`),
  KEY `CREATE_TIME` (`create_time`),
  KEY `MESSAGE_ID` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息消费结果表';

##################################################################
## RocketMQ 消息存储
## tz_message_rocketmq_{3位分表序号}
##################################################################

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_000` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_004` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_008` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_012` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_016` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_020` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_024` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_028` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_032` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_036` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_040` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_044` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_048` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_052` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_056` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_060` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_064` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_068` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_072` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_076` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_080` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_084` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_088` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_092` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_096` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_100` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_104` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_108` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_112` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_116` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_120` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_124` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_128` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_132` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_136` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_140` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_144` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_148` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_152` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_156` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_160` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_164` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_168` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_172` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_176` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_180` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_184` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_188` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_192` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_196` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_200` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_204` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_208` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_212` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_216` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_220` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_224` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_228` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_232` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_236` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_240` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_244` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_248` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';

CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq_252` (
  `id` BIGINT(20) NOT NULL COMMENT '主键Id',
  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',
  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',
  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',
  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',
  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',
  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `modify_time` DATETIME NOT NULL COMMENT '修改时间',
  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  INDEX `MSG_KEY` (`message_key` ASC),
  INDEX `CREATE_TIME` (`create_time` ASC))
  ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';
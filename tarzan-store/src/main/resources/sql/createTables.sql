##################################################################
## Tarzan 系统表
##################################################################
CREATE TABLE `tz_message_aggregate_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `time_start` datetime NOT NULL COMMENT '开始时间点',
  `time_end` datetime NOT NULL COMMENT '截至时间点',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `aggregate_type` tinyint(4) NOT NULL COMMENT '汇总类型',
  `status` tinyint(4) NOT NULL COMMENT '处理状态 0 初始 1成功 -1失败',
  `record_count` int(11) DEFAULT NULL COMMENT '处理记录数',
  `elapsed_time` bigint(20) DEFAULT NULL COMMENT '执行时间ms',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT ' 备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `PLAN_UNIQUE` (`time_start`,`mq_type`,`aggregate_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息汇总计划表';

CREATE TABLE `tz_to_check_message` (
  `tid` bigint(20) NOT NULL COMMENT '事务消息Id',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `source_time` datetime NOT NULL COMMENT '消息来源时间',
  `retry_count` smallint(8) NOT NULL COMMENT '事务检查次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`tid`),
  KEY `SOURCE_TIME` (`source_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='待检查事务状态的消息表';

CREATE TABLE `tz_to_send_message` (
  `tid` bigint(20) NOT NULL COMMENT '事务消息Id',
  `mq_type` tinyint(4) NOT NULL COMMENT 'MQ类型',
  `source_time` datetime NOT NULL COMMENT '消息来源时间',
  `retry_count` smallint(8) NOT NULL COMMENT '发送次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`tid`),
  KEY `SOURCE_TIME` (`source_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='待发送的消息表';


##################################################################
## RocketMQ 消息存储
## tz_message_rocketmq_{3位分表序号}
##################################################################

CREATE TABLE `tz_message_rocketmq_000` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_016` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_032` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_048` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_064` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_080` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_096` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_112` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_128` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_144` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_160` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_176` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  Disconnected from the target VM, address: '127.0.0.1:55410', transport: 'socket'
`topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_192` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_208` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_224` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';

CREATE TABLE `tz_message_rocketmq_240` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `message_key` varchar(80) NOT NULL COMMENT '消息key',
  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',
  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',
  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',
  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',
  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',
  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `topic` varchar(60) NOT NULL COMMENT '消息topic',
  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',
  PRIMARY KEY (`id`),
  KEY `MSG_KEY` (`message_key`(50)),
  KEY `CREATE_TIME` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';


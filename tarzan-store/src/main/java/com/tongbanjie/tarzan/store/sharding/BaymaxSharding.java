package com.tongbanjie.tarzan.store.sharding;

import com.tongbanjie.baymax.router.strategy.PartitionFunction;

/**
 * 〈Baymax分表分库工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/12
 */
public class BaymaxSharding {

    private int max;

    private int current;

    private int step;

    private String tableName;

    private String createStatement;

    private PartitionFunction partitionFunction;

    public BaymaxSharding(int max, int current, String tableName, PartitionFunction partitionFunction) {
        this.max = max;
        this.current = current;
        this.tableName = tableName;
        this.step = max / current;
    }

    public void setCreateStatement(String createStatement) {
        this.createStatement = createStatement.replace(tableName, tableName + "_%03d");
    }

    /**
     * 生成建表语句
     */
    public void getAllCreateStatement() {
        StringBuffer creates = new StringBuffer();
        for (int i = 0; i < current; i++) {
            creates.append(String.format(createStatement, i * step)).append("\n").append("\n");
        }
        System.out.println("Create Statement:\n" + creates.toString());
    }

    /**
     * 表后缀列表
     */
    public void getPostfix() {
        StringBuffer postfix = new StringBuffer();
        for (int i = 0; i < current; i++) {
            postfix.append(",").append(String.format("%03d", i * step));
        }
        System.out.println("Table's postfix: " + postfix.toString().substring(1));
    }

    /**
     * 计算id所在分表
     *
     * @param id
     */
    public int calcTablePartition(Long id) {
        return calcTablePartition(String.valueOf(id));
    }

    /**
     * 计算id所在分表
     *
     * @param id
     */
    public int calcTablePartition(String id) {
        int partition = partitionFunction.execute(id, null);
        System.out.println("Id '" + id + "', mapping table partition is " + partition);
        return partition;
    }


    public static void main(String[] args) throws InterruptedException {
        BaymaxSharding sharding = new BaymaxSharding(256, 16, "tz_message_rocketmq", new VirtualModFunction256_16());

        sharding.setCreateStatement("CREATE TABLE `tz_message_rocketmq` (\n" +
                "  `id` bigint(20) NOT NULL COMMENT '主键Id',\n" +
                "  `message_key` varchar(80) NOT NULL COMMENT '消息key',\n" +
                "  `producer_group` varchar(60) NOT NULL COMMENT '生产者group',\n" +
                "  `transaction_state` tinyint(2) NOT NULL COMMENT '事务状态',\n" +
                "  `send_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送状态',\n" +
                "  `has_aggregated` tinyint(1) NOT NULL COMMENT '是否被汇总',\n" +
                "  `message_id` varchar(45) DEFAULT NULL COMMENT 'mq消息Id',\n" +
                "  `message_body` varbinary(8000) DEFAULT NULL COMMENT '消息内容',\n" +
                "  `create_time` datetime NOT NULL COMMENT '创建时间',\n" +
                "  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
                "  `topic` varchar(60) NOT NULL COMMENT '消息topic',\n" +
                "  `tags` varchar(60) DEFAULT NULL COMMENT '消息tags',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `MSG_KEY` (`message_key`(50)),\n" +
                "  KEY `CREATE_TIME` (`create_time`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息数据表[RocketMQ]';");

        sharding.getAllCreateStatement();

        sharding.getPostfix();

        Thread.sleep(1000L);
    }
}

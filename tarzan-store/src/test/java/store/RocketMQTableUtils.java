package store;

import com.tongbanjie.tarzan.store.sharding.BaymaxSharding;
import com.tongbanjie.tarzan.store.sharding.VirtualModFunction256_16;
import com.tongbanjie.tarzan.store.sharding.VirtualModFunction256_64;
import org.junit.Before;
import org.junit.Test;

/**
 * 〈RocketMQ 分表工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/1/20
 */
public class RocketMQTableUtils {

    private BaymaxSharding sharding;

    @Before
    public void init(){
        sharding = new BaymaxSharding(256, 64, "tz_message_rocketmq", new VirtualModFunction256_64());
    }

    @Test
    public void getCreateStatement() throws InterruptedException {
        sharding.setCreateStatement("CREATE TABLE IF NOT EXISTS `tarzan`.`tz_message_rocketmq` (\n" +
                "  `id` BIGINT(20) NOT NULL COMMENT '主键Id',\n" +
                "  `message_key` VARCHAR(60) NOT NULL COMMENT '消息key',\n" +
                "  `topic` VARCHAR(60) NOT NULL COMMENT '消息topic',\n" +
                "  `producer_group` VARCHAR(60) NOT NULL COMMENT '生产者group',\n" +
                "  `transaction_state` TINYINT(2) NOT NULL COMMENT '事务状态',\n" +
                "  `send_status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '发送状态',\n" +
                "  `has_aggregated` TINYINT(1) NOT NULL COMMENT '是否被汇总',\n" +
                "  `message_id` VARCHAR(45) NULL DEFAULT NULL COMMENT 'mq消息Id',\n" +
                "  `message_body` VARBINARY(8000) NULL DEFAULT NULL COMMENT '消息内容',\n" +
                "  `create_time` DATETIME NOT NULL COMMENT '创建时间',\n" +
                "  `modify_time` DATETIME NOT NULL COMMENT '修改时间',\n" +
                "  `tags` VARCHAR(60) NULL DEFAULT NULL COMMENT '消息tags',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  INDEX `MSG_KEY` (`message_key` ASC),\n" +
                "  INDEX `CREATE_TIME` (`create_time` ASC))\n" +
                "ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COMMENT = '消息数据表[RocketMQ]';");

        System.out.println(sharding.getAllCreateStatement());

        Thread.sleep(1000L);
    }

    @Test
    public void getPostfix(){
        System.out.println(sharding.getPostfix());
    }
}

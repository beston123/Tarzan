package com.tongbanjie.tarzan.common;

/**
 * 常量定义 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public abstract class Constants {

    public static final String TARZAN_TEST_P_GROUP = "-TARZAN_TEST_P_GROUP-";

    public static final String TARZAN_TEST_TOPIC = "-TARZAN_TEST_TOPIC-";

    /******************************* 配置信息 *********************************/

    public static final String TARZAN_HOME = "TARZAN_HOME";

    public static final String TARZAN_CONFIG_PATH = "conf";

    public static final String TARZAN_CONFIG_FILE = "config.properties";

    public static final String TARZAN_CONFIG_LOG = "log4j.properties";

    public static final String TARZAN_REGISTRY_ADDRESS = "tarzan.registry.address";

    public static final String TARZAN_ROCKETMQ_NAMESRV = "tarzan.rocketmq.namesrv";

    public static final String TARZAN_SERVER_PORT = "tarzan.server.port";

    public static final String TARZAN_SERVER_ID = "tarzan.server.id";

    public static final String TARZAN_SERVER_WEIGHT = "tarzan.server.weight";

    public static final String TARZAN_STORE_CONTEXT = "META-INF/spring/tarzan-store-context.xml";

    public static final String TARZAN_CONTEXT = "META-INF/spring/tarzan-context.xml";

    /******************************* 其他 *********************************/
    public static final String CLASSPATH_PREFIX = "classpath:";

    public static final String RUN_IN_IDE = "RUN_IN_IDE";

    public static final String SEPARATOR_SLASH = "/";

    public static final String SEPARATOR_COMMA = ",";

    public static final String SEPARATOR_SEMICOLON = "";

    /******************************* MQ *********************************/
    //tid对应的key
    public static final String TARZAN_MQ_TID = "tarzan_mq_tid";

}

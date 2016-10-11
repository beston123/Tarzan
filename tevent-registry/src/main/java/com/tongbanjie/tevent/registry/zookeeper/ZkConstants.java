package com.tongbanjie.tevent.registry.zookeeper;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface ZkConstants {

    int ZK_SESSION_TIMEOUT = 5000;
    int ZK_CONNECTION_TIMEOUT = 2000;

    String ZK_REGISTRY_PATH = "/tevent";

    String ZK_SERVERS_PATH = ZK_REGISTRY_PATH + "/servers";

    String ZK_CLIENTS_PATH = ZK_REGISTRY_PATH + "/clients";

    String ZK_CHILD_SERVER_PATH = ZK_SERVERS_PATH + "/server-";

    String ZK_CHILD_CLIENT_PATH = ZK_CLIENTS_PATH + "/client-";



}

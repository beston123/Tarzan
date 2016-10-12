package com.tongbanjie.tevent.registry.zookeeper;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface ZkConstants {

    int SESSION_TIMEOUT = 5000;

    int CONNECTION_TIMEOUT = 3000;

    String REGISTRY_ROOT = "/tevent";

    String SERVERS_ROOT = REGISTRY_ROOT + "/servers";

    String CLIENTS_ROOT = REGISTRY_ROOT + "/clients";

    String SERVER_CHILD_PATH = SERVERS_ROOT + "/server-";

    String CLIENT_CHILD_PATH = CLIENTS_ROOT + "/client-";

}

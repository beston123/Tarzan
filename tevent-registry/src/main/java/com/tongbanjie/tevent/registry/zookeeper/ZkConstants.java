package com.tongbanjie.tevent.registry.zookeeper;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public abstract class ZkConstants {

    public static final int SESSION_TIMEOUT = 5000;

    public static final int CONNECTION_TIMEOUT = 3000;

    public static final String REGISTRY_ROOT = "/tevent";

    public static final String SERVERS_ROOT = REGISTRY_ROOT + "/servers";

    public static final String CLIENTS_ROOT = REGISTRY_ROOT + "/clients";

    public static final String SERVER_CHILD_PATH = SERVERS_ROOT + "/server-";

    public static final String CLIENT_CHILD_PATH = CLIENTS_ROOT + "/client-";

    public static final String SERVER_IDS_ROOT = REGISTRY_ROOT + "/serverIds";


    public final static String PATH_SEPARATOR = "/";



}

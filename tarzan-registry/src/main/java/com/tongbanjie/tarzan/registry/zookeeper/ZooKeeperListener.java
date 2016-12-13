package com.tongbanjie.tarzan.registry.zookeeper;

import java.util.List;

/**
 * zookeeper监听器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public interface ZooKeeperListener {

    /**
     * 指定目录下 节点列表变化时
     * @param parentPath        对应的父节点的路径
     * @param currentChildren   子节点的相对路径
     * @throws Exception
     */
    void onChildChanged(String parentPath, List<String> currentChildren);

    /**
     * 与zk 断开连接上时 触发
     */
    void onSessionDisconnected();

    /**
     * 与zk 连接上时 触发
     */
    void onSessionSyncConnected();

    /**
     * 与zk 建立新Session
     */
    void onNewSession();

    /**
     * 与zk 建立连接异常时 触发
     */
    void onSessionConnectError(Throwable throwable) throws Exception;

}
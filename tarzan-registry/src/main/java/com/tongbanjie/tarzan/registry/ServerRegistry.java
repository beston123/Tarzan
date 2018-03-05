package com.tongbanjie.tarzan.registry;

/**
 * 〈服务端注册中心〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/1
 */
public interface ServerRegistry extends RecoverableRegistry {

    /**
     * 注册服务端
     *
     * @param address
     * @param minId
     * @param maxId
     * @return
     */
    boolean registerServer(Address address, int minId, int maxId);

    /**
     * 是否是master
     * @return
     */
    boolean isMaster();
}

package com.tongbanjie.tevent.registry;

import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public interface Registry {

    /**
     * 启动
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 注册到指定路径
     * @param path
     * @param address
     * @return  返回注册路径
     */
    String register(String path, Address address);

    /**
     * 注册
     * @param address
     * @return  返回注册路径
     */
    String register(Address address);

    /**
     * 取消注册
     * @param path
     * @return
     */
    boolean unregister(String path);

    /**
     * 发现
     * @return
     */
    List<Address> getDiscovered();

}

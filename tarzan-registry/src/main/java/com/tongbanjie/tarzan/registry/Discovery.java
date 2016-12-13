package com.tongbanjie.tarzan.registry;

import com.tongbanjie.tarzan.common.Service;

import java.util.List;

/**
 * 〈发现者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/17
 */
public interface Discovery extends Service{

    /**
     * 是否连接上注册中心
     * @return
     */
    boolean isConnected();

    /**
     * 指定路径下的地址列表
     * @return
     */
    List<Address> getDiscovered(String parentPath);

    /**
     * 地址列表
     * @return
     */
    List<Address> getDiscovered();

    /**
     * 发现指定路径
     * @param parentPath
     * @return
     */
    List<Address> discover(String parentPath);
}

package com.tongbanjie.tevent.client;

import com.tongbanjie.tevent.common.Constants;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ClientConfig implements Cloneable{

    /**
     * 注册中心地址
     */
    private String registryAddress = System.getProperty(Constants.TEVENT_REGISTRY_ADDRESS, "192.168.1.120:2181");

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}

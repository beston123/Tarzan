package com.tongbanjie.tevent.client;

/**
 * Client配置 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ClientConfig{

    /**
     * 消息发送线程池大小
     */
    private int sendMessageThreadPoolNums = Runtime.getRuntime().availableProcessors();

    /**
     * 消息发送线程池队列容量
     */
    private int sendThreadPoolQueueCapacity = 10000;

    /**
     * Heartbeat interval in microseconds with server
     */
    private int heartbeatInterval = 1000 * 30;

    /**
     * 注册中心地址
     * 格式 ip:port
     * 例：192.168.1.120:2181
     */
    private String registryAddress;

    public ClientConfig(String registryAddress){
        this.registryAddress = registryAddress;
    }

    /*********************************** setter getter ***********************************/


    public int getSendMessageThreadPoolNums() {
        return sendMessageThreadPoolNums;
    }

    public void setSendMessageThreadPoolNums(int sendMessageThreadPoolNums) {
        this.sendMessageThreadPoolNums = sendMessageThreadPoolNums;
    }

    public int getSendThreadPoolQueueCapacity() {
        return sendThreadPoolQueueCapacity;
    }

    public void setSendThreadPoolQueueCapacity(int sendThreadPoolQueueCapacity) {
        this.sendThreadPoolQueueCapacity = sendThreadPoolQueueCapacity;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }


}

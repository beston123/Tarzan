package com.tongbanjie.tarzan.client;

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
    private int sendMessageThreadPoolNum = 4 + Runtime.getRuntime().availableProcessors();

    /**
     * 消息发送线程池队列容量
     */
    private int sendThreadPoolQueueCapacity = 20000;

    /**
     * 消息发送超时
     */
    private int sendMessageTimeout = 6 * 1000;

    /**
     * Heartbeat interval in milliseconds with server
     */
    private int heartbeatInterval = 30 * 1000;

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


    public int getSendMessageThreadPoolNum() {
        return sendMessageThreadPoolNum;
    }

    public void setSendMessageThreadPoolNum(int sendMessageThreadPoolNum) {
        this.sendMessageThreadPoolNum = sendMessageThreadPoolNum;
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

    public int getSendMessageTimeout() {
        return sendMessageTimeout;
    }

    public void setSendMessageTimeout(int sendMessageTimeout) {
        this.sendMessageTimeout = sendMessageTimeout;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "sendMessageThreadPoolNum=" + sendMessageThreadPoolNum +
                ", sendThreadPoolQueueCapacity=" + sendThreadPoolQueueCapacity +
                ", heartbeatInterval=" + heartbeatInterval +
                ", sendMessageTimeout=" + sendMessageTimeout +
                ", registryAddress='" + registryAddress + '\'' +
                '}';
    }
}

package com.tongbanjie.tarzan.client;

import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.util.NetworkUtils;

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
     * 事务反查请求容量
     */
    private int checkTransactionRequestCapacity = 2000;

    /**
     * 注册中心地址
     * 格式 ip:port
     * 例：192.168.1.120:2181
     */
    private String registryAddress;

    /**
     * 应用名称
     * 例：loan,trade,pay,merchant
     */
    private String appName;

    /**
     * 客户端Id
     * 格式：appName/hostIp
     * 例：trade/192.168.1.120
     */
    private String clientId;

    public ClientConfig(String registryAddress){
        this(registryAddress, "Unknown");
    }

    public ClientConfig(String registryAddress, String appName){
        this.registryAddress = registryAddress;
        this.appName = appName;
        initClientId();
    }

    private void initClientId(){
        String hostIp = null;
        try {
            hostIp = NetworkUtils.getLocalHostIp();
        } catch (Exception e){
            //ignore
        }
        this.clientId = this.getAppName() + Constants.SEPARATOR_SLASH + (hostIp == null ? "UnknownIp" : hostIp);
    }

    /*********************************** setter getter ***********************************/

    public int getSendMessageThreadPoolNum() {
        return sendMessageThreadPoolNum;
    }

    public int getSendThreadPoolQueueCapacity() {
        return sendThreadPoolQueueCapacity;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public int getCheckTransactionRequestCapacity() {
        return checkTransactionRequestCapacity;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public int getSendMessageTimeout() {
        return sendMessageTimeout;
    }

    public String getAppName() {
        return appName;
    }

    public String getClientId(){
        return clientId;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "sendMessageThreadPoolNum=" + sendMessageThreadPoolNum +
                ", sendThreadPoolQueueCapacity=" + sendThreadPoolQueueCapacity +
                ", sendMessageTimeout=" + sendMessageTimeout +
                ", heartbeatInterval=" + heartbeatInterval +
                ", checkTransactionRequestCapacity=" + checkTransactionRequestCapacity +
                ", registryAddress='" + registryAddress + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}

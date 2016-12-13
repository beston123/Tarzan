/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.Constants;

/**
 * Server配置
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ServerConfig {

    /**
     * 消息发送线程池大小
     */
    private int sendMessageThreadPoolNum = 32 + Runtime.getRuntime().availableProcessors() * 3;

    /**
     * 客户端管理线程池大小
     */
    private int clientManageThreadPoolNum = 8 + Runtime.getRuntime().availableProcessors();

    /**
     * 消息发送线程池队列容量
     */
    private int sendThreadPoolQueueCapacity = 100000;

    /**
     * ServerId，每个server必须唯一， 取值范围 0-31
     */
    private int serverId = Integer.parseInt(System.getProperty(Constants.TARZAN_SERVER_ID, "0"));

    /**
     * Server权重，取值范围 1~32767
     */
    private short serverWeight = Short.parseShort(System.getProperty(Constants.TARZAN_SERVER_WEIGHT, "1"));

    /**
     * 注册中心地址
     */
    private String registryAddress = System.getProperty(Constants.TARZAN_REGISTRY_ADDRESS);

    /**
     * RocketMQ nameserv 地址
     */
    private String rocketMQNamesrv =  System.getProperty(Constants.TARZAN_ROCKETMQ_NAMESRV);


    public int getSendMessageThreadPoolNum() {
        return sendMessageThreadPoolNum;
    }

    public void setSendMessageThreadPoolNum(int sendMessageThreadPoolNum) {
        this.sendMessageThreadPoolNum = sendMessageThreadPoolNum;
    }

    public int getClientManageThreadPoolNum() {
        return clientManageThreadPoolNum;
    }

    public void setClientManageThreadPoolNum(int clientManageThreadPoolNum) {
        this.clientManageThreadPoolNum = clientManageThreadPoolNum;
    }

    public int getSendThreadPoolQueueCapacity() {
        return sendThreadPoolQueueCapacity;
    }

    public void setSendThreadPoolQueueCapacity(int sendThreadPoolQueueCapacity) {
        this.sendThreadPoolQueueCapacity = sendThreadPoolQueueCapacity;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public short getServerWeight() {
        return serverWeight;
    }

    public void setServerWeight(short serverWeight) {
        this.serverWeight = serverWeight;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRocketMQNamesrv() {
        return rocketMQNamesrv;
    }

    public void setRocketMQNamesrv(String rocketMQNamesrv) {
        this.rocketMQNamesrv = rocketMQNamesrv;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "sendMessageThreadPoolNum=" + sendMessageThreadPoolNum +
                ", clientManageThreadPoolNum=" + clientManageThreadPoolNum +
                ", sendThreadPoolQueueCapacity=" + sendThreadPoolQueueCapacity +
                ", serverId=" + serverId +
                ", serverWeight=" + serverWeight +
                ", registryAddress='" + registryAddress + '\'' +
                ", rocketMQNamesrv='" + rocketMQNamesrv + '\'' +
                '}';
    }
}

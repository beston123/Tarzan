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

import com.tongbanjie.tarzan.common.Weighable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Server配置
 *
 * @author zixiao
 * @date 16/10/12
 */
@Component
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
     * 消息最大检查天数
     * [当前时间－maxCheckDays]前的消息不再检查/重发
     */
    private int messageMaxCheckDays = 7;

    /**
     * Server监听端口
     */
    @Value("${tarzan.server.port}")
    private int serverPort;

    /**
     * ServerId，每个server必须唯一
     * 取值范围 0-31
     */
    @Value("${tarzan.server.id}")
    private int serverId;

    /**
     * Server权重
     * 取值范围 1-10000
     * @see Weighable
     */
    @Value("${tarzan.server.weight}")
    private short serverWeight;

    /**
     * 注册中心地址
     */
    @Value("${tarzan.registry.address}")
    private String registryAddress;

    /**
     * RocketMQ namesrv 地址
     */
    @Value("${tarzan.rocketmq.namesrv}")
    private String rocketMQNamesrv;


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

    public int getMessageMaxCheckDays() {
        return messageMaxCheckDays;
    }

    public void setMessageMaxCheckDays(int messageMaxCheckDays) {
        this.messageMaxCheckDays = messageMaxCheckDays;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
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
                ", messageMaxCheckDays=" + messageMaxCheckDays +
                ", serverPort=" + serverPort +
                ", serverId=" + serverId +
                ", serverWeight=" + serverWeight +
                ", registryAddress='" + registryAddress + '\'' +
                ", rocketMQNamesrv='" + rocketMQNamesrv + '\'' +
                '}';
    }
}

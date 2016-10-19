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
package com.tongbanjie.tevent.server.client;


import com.tongbanjie.tevent.rpc.util.RpcHelper;
import com.tongbanjie.tevent.server.ServerConfig;
import io.netty.channel.Channel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户端 管理者<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/15
 */
public class ClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

    private static final long LockTimeoutMillis = 3000;

    private static final long ChannelExpiredTimeout = 1000 * 120;

    private final Lock groupChannelLock = new ReentrantLock();

    private final HashMap<String /* group name */, HashMap<Channel, ClientChannelInfo>> groupChannelTable =
            new HashMap<String, HashMap<Channel, ClientChannelInfo>>();

    private final Random random = new Random();

    public ClientManager(ServerConfig serverConfig) {

    }


    public HashMap<String, HashMap<Channel, ClientChannelInfo>> getGroupChannelTable() {
        HashMap<String, HashMap<Channel, ClientChannelInfo>> newGroupChannelTable =
                new HashMap<String, HashMap<Channel, ClientChannelInfo>>();
        try {
            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)){
                try {
                    newGroupChannelTable.putAll(groupChannelTable);
                } finally {
                    groupChannelLock.unlock();
                }
            }
        } catch (InterruptedException e) {
           LOGGER.error("", e);
        }
        return newGroupChannelTable;
    }


    public void scanNotActiveChannel() {
        try {
            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    for (final Entry<String, HashMap<Channel, ClientChannelInfo>> entry : this.groupChannelTable
                        .entrySet()) {
                        final String group = entry.getKey();
                        final HashMap<Channel, ClientChannelInfo> chlMap = entry.getValue();

                        Iterator<Entry<Channel, ClientChannelInfo>> it = chlMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<Channel, ClientChannelInfo> item = it.next();

                            final ClientChannelInfo info = item.getValue();

                            long diff = System.currentTimeMillis() - info.getLastUpdateTimestamp();
                            if (diff > ChannelExpiredTimeout) {
                                it.remove();
                                LOGGER.warn(
                                        "SCAN: remove expired channel[{}] from ClientManager groupChannelTable, client group name: {}",
                                        RpcHelper.parseChannelRemoteAddr(info.getChannel()), group);
                                RpcHelper.closeChannel(info.getChannel());
                            }
                        }
                    }
                }
                finally {
                    this.groupChannelLock.unlock();
                }
            }
            else {
                LOGGER.warn("ClientManager scanNotActiveChannel lock timeout");
            }
        }
        catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public void doChannelCloseEvent(final String remoteAddr, final Channel channel) {
        if (channel != null) {
            try {
                if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                    try {
                        for (final Entry<String, HashMap<Channel, ClientChannelInfo>> entry : this.groupChannelTable
                            .entrySet()) {
                            final String group = entry.getKey();
                            final HashMap<Channel, ClientChannelInfo> clientChannelInfoTable =
                                    entry.getValue();
                            final ClientChannelInfo clientChannelInfo =
                                    clientChannelInfoTable.remove(channel);
                            if (clientChannelInfo != null) {
                                LOGGER.info(
                                        "NETTY EVENT: remove channel[{}][{}] from ClientManager groupChannelTable, client group: {}",
                                        clientChannelInfo.toString(), remoteAddr, group);
                            }

                        }
                    }
                    finally {
                        this.groupChannelLock.unlock();
                    }
                }
                else {
                    LOGGER.warn("ClientManager doChannelCloseEvent lock timeout");
                }
            }
            catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        }
    }

    public void register(final String group, final ClientChannelInfo clientChannelInfo) {
        try {
            ClientChannelInfo clientChannelInfoFound = null;

            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    HashMap<Channel, ClientChannelInfo> channelTable = this.groupChannelTable.get(group);
                    if (null == channelTable) {
                        channelTable = new HashMap<Channel, ClientChannelInfo>();
                        this.groupChannelTable.put(group, channelTable);
                    }

                    clientChannelInfoFound = channelTable.get(clientChannelInfo.getChannel());
                    if (null == clientChannelInfoFound) {
                        channelTable.put(clientChannelInfo.getChannel(), clientChannelInfo);
                        LOGGER.info("New client connected, group: {} channel: {}", group,
                                clientChannelInfo.toString());
                    }
                }
                finally {
                    this.groupChannelLock.unlock();
                }

                if (clientChannelInfoFound != null) {
                    LOGGER.debug("Get heartbeat from client, group: {} channel: {}", group, clientChannelInfoFound);
                    clientChannelInfoFound.setLastUpdateTimestamp(System.currentTimeMillis());
                }
            }
            else {
                LOGGER.warn("ClientManager register lock timeout");
            }
        }
        catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public void unregister(final String group, final ClientChannelInfo clientChannelInfo) {
        try {
            if (this.groupChannelLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    HashMap<Channel, ClientChannelInfo> channelTable = this.groupChannelTable.get(group);
                    if (null != channelTable && !channelTable.isEmpty()) {
                        ClientChannelInfo old = channelTable.remove(clientChannelInfo.getChannel());
                        if (old != null) {
                            LOGGER.info("unregister a client[{}] from groupChannelTable {}", group,
                                    clientChannelInfo.toString());
                        }

                        if (channelTable.isEmpty()) {
                            this.groupChannelTable.remove(group);
                            LOGGER.info("unregister a client group[{}] from groupChannelTable", group);
                        }
                    }
                }
                finally {
                    this.groupChannelLock.unlock();
                }
            }
            else {
                LOGGER.warn("ClientManager unregister client lock timeout");
            }
        }
        catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public ClientChannelInfo pickClientRandomly(String group){
        Map<Channel, ClientChannelInfo> map = this.groupChannelTable.get(group);
        if(MapUtils.isEmpty(map)){
            return null;
        }
        List<ClientChannelInfo> clientChannelInfoList = new ArrayList<ClientChannelInfo>(map.values());
        int size = clientChannelInfoList.size();
        //TODO 均衡负载
        return clientChannelInfoList.get(random.nextInt(size));
    }
}

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

import com.tongbanjie.tevent.common.TEventVersion;
import com.tongbanjie.tevent.cluster.loadbalance.Weighable;
import io.netty.channel.Channel;

public class ClientChannelInfo implements Weighable{

    private final Channel channel;

    private String clientId;

    private int version;

    private short weight;

    private volatile long lastUpdateTimestamp = System.currentTimeMillis();


    public ClientChannelInfo(Channel channel) {
        this(channel, null, 0);
    }

    public ClientChannelInfo(Channel channel, String clientId, int version) {
        this(channel, clientId, version, Weighable.DEFAULT_WEIGHT);
    }

    public ClientChannelInfo(Channel channel, String clientId, int version, short weight) {
        this.channel = channel;
        this.clientId = clientId;
        this.version = version;
        this.weight = weight;
    }


    public Channel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public short getWeight() {
        return this.weight;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }


    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientChannelInfo that = (ClientChannelInfo) o;

        if (version != that.version) return false;
        if (weight != that.weight) return false;
        if (lastUpdateTimestamp != that.lastUpdateTimestamp) return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        return !(clientId != null ? !clientId.equals(that.clientId) : that.clientId != null);

    }

    @Override
    public int hashCode() {
        int result = channel != null ? channel.hashCode() : 0;
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (int) weight;
        result = 31 * result + (int) (lastUpdateTimestamp ^ (lastUpdateTimestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ClientChannelInfo{" +
                "channel=" + channel +
                ", clientId='" + clientId + '\'' +
                ", version=" + TEventVersion.getVersionName(version) +
                ", weight=" + weight +
                ", lastUpdateTimestamp=" + lastUpdateTimestamp +
                '}';
    }
}

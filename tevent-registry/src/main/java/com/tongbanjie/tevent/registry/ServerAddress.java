package com.tongbanjie.tevent.registry;

import com.tongbanjie.tevent.common.Weighable;

/**
 * 服务器地址 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/2
 */
public class ServerAddress implements Address{

    /**
     * 地址
     * 作为equals因子
     */
    private String address;

    /**
     * 权重
     * 取值范围 1 ~ Short.MAX_VALUE
     */
    private short weight;

    /**
     * 是否可用
     */
    private boolean enable = true;

    public ServerAddress(String address){
        this(address, Weighable.DEFAULT_WEIGHT);
    }

    public ServerAddress(String address, short weight){
        this.address = address;
        this.weight = weight <= 0 ? Weighable.DEFAULT_WEIGHT : weight;
    }

    public ServerAddress(String ip, int port){
        this(ip + ":" +port);
    }

    public ServerAddress(String ip, int port, short weight){
        this(ip + ":" +port, weight);
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public short getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(short weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerAddress)) return false;

        ServerAddress that = (ServerAddress) o;

        if (weight != that.weight) return false;
        if (enable != that.enable) return false;
        return !(address != null ? !address.equals(that.address) : that.address != null);

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (int) weight;
        result = 31 * result + (enable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "address='" + address + '\'' +
                ", weight=" + weight +
                ", enable=" + enable +
                '}';
    }
}

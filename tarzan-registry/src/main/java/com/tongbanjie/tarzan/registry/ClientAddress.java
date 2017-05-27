package com.tongbanjie.tarzan.registry;

import com.tongbanjie.tarzan.common.TarzanVersion;
import com.tongbanjie.tarzan.common.Weighable;

/**
 * 〈客户端地址〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/5/9
 */
public class ClientAddress implements Address {

    private static final long serialVersionUID = -4571646224647116972L;

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

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 版本号
     */
    private Integer version;

    public ClientAddress(String appName, String address, short weight){
        this.appName = appName;
        this.address = address;
        this.weight = weight <= 0 ? Weighable.DEFAULT_WEIGHT : weight;
        this.version = TarzanVersion.CURRENT.getValue();
    }

    public ClientAddress(String appName, String address){
        this(appName, address, Weighable.DEFAULT_WEIGHT);
    }

    @Override
    public String getAddress() {
        return address;
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
        return weight;
    }

    @Override
    public void setWeight(short weight) {
        this.weight = weight;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientAddress)) return false;

        ClientAddress that = (ClientAddress) o;

        if (weight != that.weight) return false;
        if (enable != that.enable) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        return !(version != null ? !version.equals(that.version) : that.version != null);

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (int) weight;
        result = 31 * result + (enable ? 1 : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientAddress{" +
                "address='" + address + '\'' +
                ", weight=" + weight +
                ", enable=" + enable +
                ", appName='" + appName + '\'' +
                ", version=" + version +
                '}';
    }
}

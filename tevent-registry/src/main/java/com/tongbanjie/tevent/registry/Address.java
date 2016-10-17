package com.tongbanjie.tevent.registry;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class Address implements Serializable{

    /**
     * 权重默认值
     */
    public static final short DEFAULT_WEIGHT = 1;

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

    private Address(){}


    public Address(String address){
        this(address, DEFAULT_WEIGHT);
    }

    public Address(String address, short weight){
        this.address = address;
        this.weight = weight <= 0 ? DEFAULT_WEIGHT : weight;
    }

    public Address(String ip, int port){
        this(ip + ":" +port);
    }

    public Address(String ip, int port, short weight){
        this(ip + ":" +port, weight);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address1 = (Address) o;

        return !(address != null ? !address.equals(address1.address) : address1.address != null);

    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "{" +
                "address='" + address + '\'' +
                ", weight=" + weight +
                '}';
    }
}

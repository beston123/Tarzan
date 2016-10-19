package com.tongbanjie.tevent.registry;

import com.tongbanjie.tevent.common.Weighable;

import java.io.Serializable;

/**
 * 服务地址 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class Address implements Serializable, Weighable {

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
        this(address, Weighable.DEFAULT_WEIGHT);
    }

    public Address(String address, short weight){
        this.address = address;
        this.weight = weight <= 0 ? Weighable.DEFAULT_WEIGHT : weight;
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

    @Override
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

        if (weight != address1.weight) return false;
        return address.equals(address1.address);

    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + (int) weight;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "address='" + address + '\'' +
                ", weight=" + weight +
                '}';
    }
}

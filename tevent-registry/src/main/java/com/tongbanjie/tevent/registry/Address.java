package com.tongbanjie.tevent.registry;

import com.tongbanjie.tevent.common.Weighable;

import java.io.Serializable;

/**
 * 地址 接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public interface Address extends Serializable, Weighable{

    String getAddress();

    void setAddress(String address);

    boolean isEnable();

    void setEnable(boolean enable);

}

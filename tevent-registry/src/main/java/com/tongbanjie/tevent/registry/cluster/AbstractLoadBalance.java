package com.tongbanjie.tevent.registry.cluster;

import com.tongbanjie.tevent.registry.Address;

/**
 * 负载均衡 抽象类 <p>
 * 〈功能详细描述〉
 * @URL http://camel.apache.org/load-balancer.html
 * @author zixiao
 * @date 16/10/18
 */
public abstract class AbstractLoadBalance implements LoadBalance<Address> {

    protected Address lastSelected;

    @Override
    public Address getLastSelected() {
        return this.lastSelected;
    }

}

package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;

/**
 * 负载均衡 抽象类 <p>
 * 〈功能详细描述〉
 * @URL http://camel.apache.org/load-balancer.html
 * @author zixiao
 * @date 16/10/18
 */
public abstract class AbstractLoadBalance<T extends Weighable> implements LoadBalance<T> {

    protected T lastSelected;

    @Override
    public T getLastSelected() {
        return this.lastSelected;
    }

}

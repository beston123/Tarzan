package com.tongbanjie.tevent.registry.cluster;

import com.tongbanjie.tevent.registry.Address;

import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public class FailoverInvoker implements Failoverable<Address> {

    protected final LoadBalance<Address> loadBalance;

    public FailoverInvoker(LoadBalance<Address> loadBalance){
        this.loadBalance = loadBalance;
    }

    @Override
    public Address invoke(List list, int retryTimes) {
        return loadBalance.select(list);
    }

}

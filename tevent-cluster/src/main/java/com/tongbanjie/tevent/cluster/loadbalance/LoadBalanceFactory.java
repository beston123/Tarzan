package com.tongbanjie.tevent.cluster.loadbalance;

import com.tongbanjie.tevent.common.Weighable;

import java.util.ArrayList;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/19
 */
public class LoadBalanceFactory {

    public static LoadBalance getLoadBalance(LoadBalanceStrategy strategy){
        LoadBalance loadBalance = null;
        switch (strategy){
            case Random:
                loadBalance = new RandomLoadBalance();
                break;
            case RoundRobin:
                loadBalance = new RoundRobinLoadBalance();
                break;
            case WeightedRandom:
                loadBalance = new WeightedRandomLoadBalance();
                break;
            default:
                loadBalance = new RandomLoadBalance();
                break;
        }
        return loadBalance;
    }

    public static void main(String[] args) {
        LoadBalance<Weighable> addressLoadBalance = LoadBalanceFactory.getLoadBalance(LoadBalanceStrategy.Random);
        addressLoadBalance.select(new ArrayList<Weighable>());
    }

}

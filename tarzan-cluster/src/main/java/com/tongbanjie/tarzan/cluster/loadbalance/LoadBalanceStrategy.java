package com.tongbanjie.tarzan.cluster.loadbalance;

/**
 * 负载均衡 策略<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/19
 */
public enum  LoadBalanceStrategy {

    Random,
    RoundRobin,
    WeightedRandom,
    WeightedRoundRobin
}

package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 基于［加权轮询算法］的负载均衡<p>
 * Weighted RoundRobin
 *
 * @author zixiao
 * @date 17/2/4
 */
public class WeightedRoundRobinLoadBalance<T extends Weighable> extends RoundRobinLoadBalance<T> {

    @Override
    public T select(List<T> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        //如果权重相同，则使用RoundRobin算法
        if(WeightedUtils.isSameWeight(list)){
            return super.select(list);
        }
        //如果权重不相同，则使用Weighted RoundRobin算法
        return super.select(WeightedUtils.getVirtualList(list));
    }
}

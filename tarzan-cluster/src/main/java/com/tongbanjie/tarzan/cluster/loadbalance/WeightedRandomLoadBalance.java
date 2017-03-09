package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 基于［加权随机算法］的负载均衡<p>
 * Weighted Random
 *
 * @author zixiao
 * @date 16/10/18
 */
public class WeightedRandomLoadBalance<T extends Weighable> extends RandomLoadBalance<T>{

    @Override
    public T select(List<T> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        //如果权重相同，则使用Random算法
        if(WeightedUtils.isSameWeight(list)){
            return super.select(list);
        }
        //如果权重不相同，则使用Weighted Random算法
        return super.select(WeightedUtils.getVirtualList(list));
    }

}

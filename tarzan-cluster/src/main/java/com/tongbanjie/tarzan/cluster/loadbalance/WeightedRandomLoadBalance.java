package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import com.tongbanjie.tarzan.common.util.ThreadLocalRandom;
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

        int size = list.size();
        int[] weights = new int[size];
        int firstWeight = list.get(0).getWeight();

        boolean sameWeight = true;
        weights[0] = firstWeight;
        int totalWeight = firstWeight;
        for(int i=1; i<size; i++){
            weights[i] = list.get(i).getWeight();
            int weight = weights[i];
            totalWeight += weight;
            if(weight != firstWeight){
                sameWeight = false;
            }
        }
        //如果权重相同，则使用Random算法
        if(sameWeight){
            return super.select(list);
        }
        //如果权重不相同，则使用Weighted Random算法
        int offset = ThreadLocalRandom.current().nextInt(totalWeight);
        for(int i=0; i<size; i++){
            offset -= weights[i];
            if(offset < 0){
                return list.get(i);
            }
        }
        return super.select(WeightedUtils.getVirtualList(list));
    }

}

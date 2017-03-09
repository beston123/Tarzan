package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 基于［随机算法］的负载均衡<p>
 * Random
 *
 * @author zixiao
 * @date 16/10/18
 */
public class RandomLoadBalance<T extends Weighable> extends AbstractLoadBalance<T>{

    private final Random random = new Random();

    @Override
    public T select(List<T> list) {
        return doSelect(list);
    }

    private T doSelect(List<T> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        int size = list.size();
        if(size == 1){
            lastSelected = list.get(0);
        }else if(size == 2){
            //轮询
            lastSelected = list.get(0).equals(lastSelected) ? list.get(1) : list.get(0);
        }else{
            //随机
            lastSelected = list.get(random.nextInt(size));
        }
        return lastSelected;
    }

}

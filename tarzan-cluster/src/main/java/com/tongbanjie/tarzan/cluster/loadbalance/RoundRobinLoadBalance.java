package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 基于［轮询算法］的负载均衡 <p>
 * RoundRobin
 *
 * @author zixiao
 * @date 16/10/18
 */
public class RoundRobinLoadBalance<T extends Weighable> extends AbstractLoadBalance<T> {

    protected int lastIndex = -1;

    @Override
    public T select(List<T> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        int size = list.size();
        if(size == 1){
            lastSelected = list.get(0);
        }else{ //size > 1
            lastSelected = doRoundRobin(list);
        }
        return lastSelected;
    }

    private T doRoundRobin(List<T> list){
        //初次执行
        if(lastIndex == -1){
            lastIndex ++;
            return list.get(lastIndex);
        }

        int maxIndex = list.size()-1;

        //list数据有变动，重新开始轮询
        if( lastIndex >= maxIndex || !list.get(lastIndex).equals(lastSelected) ){
            lastIndex = -1;
            return doRoundRobin(list);
        }

        //重新从0开始
        if (lastIndex == (list.size()-1)) {
            lastIndex = 0;
            return list.get(lastIndex);
        }

        //正常执行 RoundRobin
        lastIndex ++;

        return list.get(lastIndex);

    }

}

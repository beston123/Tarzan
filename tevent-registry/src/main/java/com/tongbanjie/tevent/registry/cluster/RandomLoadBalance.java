package com.tongbanjie.tevent.registry.cluster;

import com.tongbanjie.tevent.registry.Address;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 随机算法 负载均衡<p>
 * Random
 *
 * @author zixiao
 * @date 16/10/18
 */
public class RandomLoadBalance extends AbstractLoadBalance{

    private final Random random = new Random();

    @Override
    public Address select(List<Address> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        int size = list.size();
        if(size == 1){
            lastSelected = list.get(0);
        }else if(size == 2){
            //轮询
            lastSelected = lastSelected == list.get(0) ? list.get(1) : list.get(0);
        }else{
            //随机
            lastSelected = list.get(random.nextInt(size));
        }
        return lastSelected;
    }

}

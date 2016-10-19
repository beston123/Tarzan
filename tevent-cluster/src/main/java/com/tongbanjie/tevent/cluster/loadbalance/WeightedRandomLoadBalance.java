package com.tongbanjie.tevent.cluster.loadbalance;

import com.tongbanjie.tevent.common.Weighable;
import com.tongbanjie.tevent.registry.Address;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Weighted Random <p>
 *
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
        if(isSameWeight(list)){
            return super.select(list);
        }
        //如果权重相同，则使用Weighted Random算法
        List<T> virtualList = new ArrayList<T>();
        for (T item : list){
            short weight = item.getWeight();
            for(int i=0; i< weight; i++){
                virtualList.add(item);
            }
        }
        return super.select(virtualList);
    }

    private boolean isSameWeight(List<? extends Weighable> list){
        boolean sameWeight = true;
        short firstWeight = 0;
        for(Weighable item : list){
            short weight = item.getWeight();
            if(firstWeight == 0){
                firstWeight = weight;
            }
            if(firstWeight != weight){
                sameWeight = false;
            }

        }
        return sameWeight;
    }


    public static void main(String[] args) {
        List<Address> list = new ArrayList<Address>();

        init(list);
        LoadBalance<Address> loadBalance = new WeightedRandomLoadBalance<Address>();

        for(int j=0; j< 100; j++){
            System.out.println(">>>>"+j+":"+loadBalance.select(list));
        }
    }

    private static void init(List<Address> list){
        for(int i=0; i<5; i++){
            short weight = Short.parseShort((i+1)+"");
            Address address1 = new Address("192.168.1.1", i , weight);
            list.add(address1);
        }
    }
}

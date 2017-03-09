package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈权重工具类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/2/4
 */
public class WeightedUtils{

    /**
     * 判断对象的权重是否相同
     * @param list
     * @return
     */
    public static boolean isSameWeight(List<? extends Weighable> list){
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

    /**
     * 按权重生成虚拟列表
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends Weighable> List<T> getVirtualList(List<T> list){
        List<T> virtualList = new ArrayList<T>();
        for (T item : list){
            short weight = item.getWeight();
            for(int i=0; i< weight; i++){
                virtualList.add(item);
            }
        }
        return virtualList;
    }
}

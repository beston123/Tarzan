package com.tongbanjie.tevent.registry.cluster;

import com.tongbanjie.tevent.registry.Address;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    protected int lastIndex = -1;

    @Override
    public Address select(List<Address> list) {
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

    private Address doRoundRobin(List<Address> list){
        //初次执行
        if(lastIndex == -1){
            lastIndex ++;
            return list.get(lastIndex);
        }

        int maxIndex = list.size()-1;

        //list数据有变动，重新开始轮询
        if( lastIndex >= maxIndex || list.get(lastIndex) != lastSelected ){
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

    public static void main(String[] args) {
        List<Address> list = new ArrayList<Address>();

        init(list);
        LoadBalance<Address> loadBalance = new RoundRobinLoadBalance();

        for(int j=0; j< 12; j++){
            System.out.println(">>>>"+j+":"+loadBalance.select(list));
        }
        System.out.println("************************ list changed ************************");

        Collections.reverse(list);
        for(int j=12; j< 24; j++){
            System.out.println(">>>>"+j+":"+loadBalance.select(list));
        }
    }

    private static void init(List<Address> list){
        for(int i=0; i<5; i++){
            Address address1 = new Address("192.168.1.1", i);
            list.add(address1);
        }
    }

}

package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import com.tongbanjie.tarzan.common.extension.ExtensionLoader;

import java.util.ArrayList;

/**
 * 负载均衡算法 工厂 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/19
 */
public class LoadBalanceFactory {

    private static ExtensionLoader<LoadBalance> extensionLoader = ExtensionLoader.load(LoadBalance.class);

    public static LoadBalance get(){
        return extensionLoader.get();
    }

    private static LoadBalance get(String name){
        return extensionLoader.find(name);
    }

    public static void main(String[] args) {
        LoadBalance<Weighable> addressLoadBalance = get();
        addressLoadBalance.select(new ArrayList<Weighable>());
    }

}

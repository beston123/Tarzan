package com.tongbanjie.tevent.cluster.loadbalance;

import com.tongbanjie.tevent.common.Weighable;

import java.util.List;

/**
 *  负载均衡接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public interface LoadBalance<T extends Weighable> {

    T select(List<T> list);

    T getLastSelected();

}

package com.tongbanjie.tarzan.cluster.loadbalance;

import com.tongbanjie.tarzan.common.Weighable;
import com.tongbanjie.tarzan.common.extension.SPI;

import java.util.List;

/**
 *  负载均衡接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
@SPI("weightedRandom")
public interface LoadBalance<T extends Weighable> {

    T select(List<T> list);

    T getLastSelected();

}

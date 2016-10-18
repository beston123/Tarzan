package com.tongbanjie.tevent.registry.cluster;

import java.util.List;

/**
 *  负载均衡接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public interface LoadBalance<T> {

    T select(List<T> list);

    T getLastSelected();

}

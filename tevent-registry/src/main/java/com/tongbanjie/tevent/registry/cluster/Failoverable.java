package com.tongbanjie.tevent.registry.cluster;

import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/18
 */
public interface Failoverable<T> {

    T invoke(List<T> list, int retryTimes);

}

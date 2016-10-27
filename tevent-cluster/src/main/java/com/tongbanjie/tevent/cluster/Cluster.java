package com.tongbanjie.tevent.cluster;

import java.util.List;

/**
 * 集群接口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/19
 */
public interface Cluster<R /* 返回值类型 */, T /* 调用的目标对象类型*/, A /* 参数类型 */, C /* 回调函数 */> {

    R invokeSync(long timeoutMillis, int retryTimes, List<T> targetList, A arg) throws Exception;

    void invokeAsync(long timeoutMillis, int retryTimes, List<T> targetList, A arg, C callback) throws Exception;

    T selectOne();

}

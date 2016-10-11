package com.tongbanjie.tevent.registry;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/10
 */
public interface ServiceRegistry {

    void start() throws Exception;

    void register(String address);
}

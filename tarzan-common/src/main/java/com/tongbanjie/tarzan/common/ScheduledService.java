package com.tongbanjie.tarzan.common;

/**
 * 〈定时执行服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/5
 */
public interface ScheduledService extends Service{

    void schedule() throws Exception;
}

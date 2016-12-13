package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.ScheduledService;
import com.tongbanjie.tarzan.common.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈定时任务管理器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/5
 */
public class ScheduledServiceManager implements Service{

    private List<ScheduledService> scheduledServices;

    public ScheduledServiceManager(){
        scheduledServices = new ArrayList<ScheduledService>();
    }

    @Override
    public void start() throws Exception {
        for(ScheduledService service : scheduledServices){
            service.start();
        }
    }

    @Override
    public void shutdown() {
        for(ScheduledService service : scheduledServices){
            service.shutdown();
        }
    }

    public ScheduledServiceManager add(ScheduledService service){
        scheduledServices.add(service);
        return this;
    }

}

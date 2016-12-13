package com.tongbanjie.tarzan.admin.service.impl;

import com.tongbanjie.tarzan.admin.component.AdminDiscovery;
import com.tongbanjie.tarzan.admin.service.ServerManageService;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.ServerAddress;
import com.tongbanjie.tarzan.registry.zookeeper.ZkConstants;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈Server管理服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/17
 */
@Service
public class ServerManageServiceImpl implements ServerManageService{

    @Autowired
    private AdminDiscovery adminDiscovery;

    @Override
    public Result<List<ServerAddress>> getAllServers() {
        List<Address> list = adminDiscovery.getDiscovered(ZkConstants.SERVERS_ROOT);
        return Result.buildSucc(castToServerAddress(list));
    }

    @Override
    public Result<List<ServerAddress>> getServerIds() {
        List<Address> list = adminDiscovery.discover(ZkConstants.SERVER_IDS_ROOT);
        return Result.buildSucc(castToServerAddress(list));
    }

    private List<ServerAddress> castToServerAddress(List<Address> list){
        List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>(list.size());
        for(Address address : list){
            if(address instanceof ServerAddress){
                serverAddressList.add((ServerAddress)address);
            }
        }
        return serverAddressList;
    }

    @Override
    public Result<Void> deleteServerId(int serverId){
        Validate.notNull(serverId, "server Id is null");
        ServerAddress toDelete = adminDiscovery.getByServerId(serverId);
        if(toDelete == null){
            return Result.buildSucc(null);
        }
        Result<Void> canBeDelete = canBeDelete(toDelete);
        if(!canBeDelete.isSuccess()){
            return canBeDelete;
        }
        if(adminDiscovery.deleteServerId(serverId)){
            return Result.buildSucc(null);
        }else{
            return Result.buildFail(FailResult.SYSTEM);
        }
    }

    private Result<Void> canBeDelete(ServerAddress toDelete){
        List<ServerAddress> serverList = this.getAllServers().getData();
        if(serverList == null){
            return Result.buildFail(FailResult.SYSTEM);
        }
        for(ServerAddress address : serverList){
            if(toDelete.getAddress().equals(address.getAddress())){
                return Result.buildFail(FailResult.BUSINESS, "ServerId使用中");
            }
        }
        return Result.buildSucc(null);
    }

}

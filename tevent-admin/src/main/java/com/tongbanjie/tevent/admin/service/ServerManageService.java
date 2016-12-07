package com.tongbanjie.tevent.admin.service;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.registry.ServerAddress;

import java.util.List;

/**
 * 〈Server管理服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/17
 */
public interface ServerManageService {

    /**
     * 查询所有Server列表
     * @return
     */
    Result<List<ServerAddress>> getAllServers();

    /**
     * 查询所有ServerId列表
     * @return
     */
    Result<List<ServerAddress>> getServerIds();

    /**
     * 删除ServerId
     * 谨慎操作
     * @param serverId
     * @return
     */
    Result<Void> deleteServerId(int serverId);
}

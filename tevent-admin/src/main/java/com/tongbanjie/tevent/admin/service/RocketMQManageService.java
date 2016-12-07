package com.tongbanjie.tevent.admin.service;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.message.RocketMQMessage;

import java.util.List;

/**
 * 〈RocketMQ管理服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/22
 */
public interface RocketMQManageService {

    Result<RocketMQMessage> queryById(Long id);

    Result<List<RocketMQMessage>> queryByMessageKey(String messageKey);

}

package com.tongbanjie.tarzan.admin.service.impl;

import com.tongbanjie.tarzan.admin.service.RocketMQManageService;
import com.tongbanjie.tarzan.admin.component.AdminServerComponent;
import com.tongbanjie.tarzan.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tarzan.common.FailResult;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.exception.RpcException;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.CustomHeader;
import com.tongbanjie.tarzan.rpc.protocol.header.QueryMessageHeader;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈RocketMQ管理服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/22
 */
@Service
public class RocketMQManageServiceImpl implements RocketMQManageService {

    @Autowired
    private AdminServerComponent adminServerComponent;

    private static final long timeOut = 6000L;

    private final MQType mqType = MQType.ROCKET_MQ;

    @Override
    public Result<RocketMQMessage> queryById(Long id) {
        Result<RocketMQMessage> result;
        try {
            Validate.notNull(id, "id 不能为空");
            QueryMessageHeader queryMessageHeader = buildRequestHeader();
            queryMessageHeader.setTransactionId(id);
            RpcCommand response = invokeSync(buildRequest(queryMessageHeader));
            if (response.getCmdCode() == ResponseCode.SUCCESS) {
                RocketMQMessage mqMessage = null;
                if (response.getBody() != null) {
                    mqMessage = response.getBody(RocketMQMessage.class);
                }
                result = Result.buildSucc(mqMessage);
            } else {
                result = Result.buildFail(FailResult.SYSTEM, response.getRemark());
            }
        } catch (RpcTimeoutException e) {
            result = Result.buildFail(FailResult.TIMEOUT, e.getMessage());
        } catch (RpcException e) {
            result = Result.buildFail(FailResult.RPC, e.getMessage());
        } catch (IllegalArgumentException e){
            result = Result.buildFail(FailResult.PARAMETER, e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<RocketMQMessage>> queryByMessageKey(String messageKey) {
        Result<List<RocketMQMessage>> result;
        try {
            Validate.notEmpty(messageKey, "messageKey 不能为空");
            QueryMessageHeader queryMessageHeader = buildRequestHeader();
            queryMessageHeader.setMessageKey(messageKey);
            RpcCommand response = invokeSync(buildRequest(queryMessageHeader));
            if (response.getCmdCode() == ResponseCode.SUCCESS) {
                List<RocketMQMessage> mqMessageList = (List<RocketMQMessage>)response.getBody(ArrayList.class);
                result = Result.buildSucc(mqMessageList);
            } else {
                result = Result.buildFail(FailResult.SYSTEM, response.getRemark());
            }
        } catch (RpcTimeoutException e) {
            result = Result.buildFail(FailResult.TIMEOUT, e.getMessage());
        } catch (RpcException e) {
            result = Result.buildFail(FailResult.RPC, e.getMessage());
        } catch (IllegalArgumentException e){
            result = Result.buildFail(FailResult.PARAMETER, e.getMessage());
        }
        return result;
    }

    private QueryMessageHeader buildRequestHeader(){
        QueryMessageHeader queryMessageHeader = new QueryMessageHeader();
        queryMessageHeader.setMqType(mqType);
        return queryMessageHeader;
    }

    private RpcCommand buildRequest(CustomHeader customHeader){
        return RpcCommandBuilder.buildRequest(RequestCode.QUERY_MESSAGE, customHeader);
    }

    private RpcCommand invokeSync(RpcCommand request) throws RpcException{
        return adminServerComponent.invokeSync(request, timeOut);
    }

}

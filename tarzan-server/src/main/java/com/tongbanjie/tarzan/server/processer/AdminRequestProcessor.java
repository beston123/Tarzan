package com.tongbanjie.tarzan.server.processer;

import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.MQMessage;
import com.tongbanjie.tarzan.common.message.MQType;
import com.tongbanjie.tarzan.rpc.exception.RpcCommandException;
import com.tongbanjie.tarzan.rpc.netty.NettyRequestProcessor;
import com.tongbanjie.tarzan.rpc.protocol.RequestCode;
import com.tongbanjie.tarzan.rpc.protocol.ResponseCode;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommandBuilder;
import com.tongbanjie.tarzan.rpc.protocol.header.AdminRequestHeader;
import com.tongbanjie.tarzan.server.ServerController;
import com.tongbanjie.tarzan.server.handler.MQMessageHandler;
import com.tongbanjie.tarzan.server.handler.MQMessageHandlerFactory;
import com.tongbanjie.tarzan.server.transaction.TransactionCheckExecutor;
import com.tongbanjie.tarzan.store.StoreManager;
import com.tongbanjie.tarzan.store.service.StoreService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 〈admin的请求处理器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 17/5/31
 */
@Component
public class AdminRequestProcessor implements NettyRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRequestProcessor.class);

    @Autowired
    private TransactionCheckExecutor transactionCheckExecutor;

    @Autowired
    private StoreManager storeManager;

    @Autowired
    private ServerController serverController;

    @Override
    public RpcCommand processRequest(ChannelHandlerContext ctx, RpcCommand request) throws Exception {
        switch (request.getCmdCode()) {
            case RequestCode.ADMIN_CHECK_TRANSACTION_ONCE:
                return this.checkTransaction(ctx, request);
            case RequestCode.ADMIN_SEND_MESSAGE_ONCE:
                return this.sendMessage(ctx, request);
            default:
                LOGGER.warn("Invalid request，requestCode："+request.getCmdCode());
                break;
        }
        return RpcCommandBuilder.buildResponse(ResponseCode.INVALID_REQUEST,
                "Invalid request，requestCode：" + request.getCmdCode());
    }

    private RpcCommand checkTransaction(ChannelHandlerContext ctx, RpcCommand request) {
        //1、解析消息头
        AdminRequestHeader messageHeader = (AdminRequestHeader)request.decodeCustomHeader(AdminRequestHeader.class);
        validateMessage(messageHeader);

        //2、检查事务状态
        Result<Void> result = doCheckTransaction(messageHeader.getTransactionId(), messageHeader.getMqType());
        RpcCommand response;
        if(result.isSuccess()){
            response = RpcCommandBuilder.buildSuccess(null);
        }else{
            response = RpcCommandBuilder.buildFail("检查事务状态失败,"+result.getErrorDetail());
        }
        return response;
    }

    private RpcCommand sendMessage(ChannelHandlerContext ctx, RpcCommand request) {
        //1、解析消息头
        AdminRequestHeader messageHeader = (AdminRequestHeader)request.decodeCustomHeader(AdminRequestHeader.class);
        validateMessage(messageHeader);

        //2、获取事件处理者
        MQMessageHandler producer = getHandler(messageHeader.getMqType());
        if(producer == null){
            return RpcCommandBuilder.buildResponse(ResponseCode.SYSTEM_ERROR,
                    "System error：can not find a producer to handle the message {}" + messageHeader);
        }
        //3、查询消息并发送
        StoreService storeService = storeManager.getMQStoreService(messageHeader.getMqType());
        Result<MQMessage> result = storeService.get(messageHeader.getTransactionId());
        if(result.getData() == null){
            return RpcCommandBuilder.buildFail("消息不存在");
        }
        Result<String> sendRet = producer.sendMessage(result.getData());
        if(!sendRet.isSuccess()){
            return RpcCommandBuilder.buildFail("消息发送失败");
        }
        return RpcCommandBuilder.buildSuccess(sendRet.getData());
    }

    private void validateMessage(AdminRequestHeader header) throws RpcCommandException{
        if(header == null){
            throw new RpcCommandException("Param error: messageHeader can not be null");
        }
        if(header.getMqType() == null){
            throw new RpcCommandException("Param error: mqType can not be null");
        }
    }

    private MQMessageHandler getHandler(MQType mqType){
        return MQMessageHandlerFactory.getInstance().getAndCreate(mqType, this.serverController);
    }

    private Result<Void> doCheckTransaction(Long tid, MQType mqType){
        StoreService storeService = this.storeManager.getMQStoreService(mqType);
        if(storeService == null){
            return Result.buildFail("", "不支持的MQType:'"+mqType.name()+"'");
        }

        Result<MQMessage> result = storeService.get(tid);
        if(result.getData() == null){
            return Result.buildFail("", "消息不存在");
        }

        try {
            this.transactionCheckExecutor.gotoCheck(result.getData().getProducerGroup(), mqType, result.getData());
            return Result.buildSucc(null);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("事务状态失败,tid:" + tid + ", mqType:" + mqType, e);
            return Result.buildFail("", e.getMessage());
        }
    }
}

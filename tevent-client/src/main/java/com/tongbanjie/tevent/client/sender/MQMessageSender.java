package com.tongbanjie.tevent.client.sender;

import com.tongbanjie.tevent.common.body.MQBody;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.protocol.header.CheckTransactionStateHeader;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/13
 */
public interface MQMessageSender<T extends MQBody> {

    int checkThreadPoolCoreSize = 1;

    int checkThreadPoolMaxSize = 1;

    int checkRequestHoldMax = 1000;

    TransactionCheckListener transactionCheckListener();

    void checkTransactionState(final String addr, final T mqBody,
                               final CheckTransactionStateHeader requestHeader,
                               final RpcClient rpcClient);


}

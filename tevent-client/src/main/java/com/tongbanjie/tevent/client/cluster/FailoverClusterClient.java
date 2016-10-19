package com.tongbanjie.tevent.client.cluster;


import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tevent.rpc.InvokeCallback;
import com.tongbanjie.tevent.rpc.RpcClient;
import com.tongbanjie.tevent.rpc.exception.RpcConnectException;
import com.tongbanjie.tevent.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tevent.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tevent.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tevent.rpc.protocol.RpcCommand;

import java.util.List;

/**
 * Failover策略 <p>
 *
 *
 * @author zixiao
 * @date 16/10/19
 */
public class FailoverClusterClient extends ClusterClient {


    public FailoverClusterClient(ThreadLocal<LoadBalance<Address>> loadBalance, RpcClient rpcClient) {
        super(loadBalance, rpcClient);
    }


    @Override
    public RpcCommand invokeSync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException {
        if(retryTimes < 0){
            retryTimes = 0;
        }

        long start = System.currentTimeMillis();
        while (retryTimes >= 0 && timeoutMillis <= (System.currentTimeMillis() - start)){
            try {
                Address address = select(targetList);
                RpcCommand result = doInvokeSync(timeoutMillis, address, arg);
                return result;
            } catch (RpcConnectException e) {
                //只对连接超时异常 failover
                retryTimes--;
            }

        }
        return null;
    }

    @Override
    public void invokeAsync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException {
        if(retryTimes < 0){
            retryTimes = 0;
        }

        long start = System.currentTimeMillis();
        while (retryTimes >= 0 && timeoutMillis <= (System.currentTimeMillis() - start)){
            try {
                Address address = select(targetList);
                doInvokeAsync(timeoutMillis, address, arg, callback);
            } catch (RpcConnectException e) {
                //只对连接超时异常 failover
                retryTimes--;
            }
        }
    }


}

package com.tongbanjie.tevent.client.cluster;


import com.tongbanjie.tevent.registry.Address;
import com.tongbanjie.tevent.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tevent.registry.Registry;
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
 * 失败转移，当出现失败，重试其它服务
 *
 * @author zixiao
 * @date 16/10/19
 */
public class FailoverClusterClient extends ClusterClient {

    public FailoverClusterClient(ThreadLocal<LoadBalance<Address>> loadBalance, RpcClient rpcClient, Registry registry) {
        super(loadBalance, rpcClient, registry);
    }


    @Override
    public RpcCommand invokeSync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException {
        if(retryTimes < 0){
            retryTimes = 0;
        }

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) <= timeoutMillis){
            try {
                Address address = select(targetList);
                RpcCommand result = doInvokeSync(timeoutMillis, address, arg);
                return result;
            } catch (RpcConnectException e) {
                //连接超时异常 failover
                if(retryTimes > 0){
                    retryTimes--;
                }else {
                    throw e;
                }
            } catch (RpcTooMuchRequestException e) {
                //连接超时异常 failover
                if(retryTimes > 0){
                    retryTimes--;
                }else {
                    throw e;
                }
            }

        }
        throw new RpcTimeoutException("Invoke timeout, costs "+(System.currentTimeMillis() - start)+"ms");
    }

    @Override
    public void invokeAsync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException {
        if(retryTimes < 0){
            retryTimes = 0;
        }

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) <= timeoutMillis){
            try {
                Address address = select(targetList);
                doInvokeAsync(timeoutMillis, address, arg, callback);
                return;
            } catch (RpcConnectException e) {
                //连接超时异常 failover
                if(retryTimes > 0){
                    retryTimes--;
                }else {
                    throw e;
                }
            } catch (RpcTooMuchRequestException e) {
                //连接超时异常 failover
                if(retryTimes > 0){
                    retryTimes--;
                }else {
                    throw e;
                }
            }
        }
        throw new RpcTimeoutException("Invoke timeout, costs "+(System.currentTimeMillis() - start)+"ms");
    }

}

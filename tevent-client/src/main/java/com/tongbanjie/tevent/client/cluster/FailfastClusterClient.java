package com.tongbanjie.tevent.client.cluster;

import com.tongbanjie.tevent.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tevent.registry.Address;
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
 * Failfast 策略 <p>
 * 快速失败，只发起一次调用，失败立即报错
 *
 * @author zixiao
 * @date 16/10/20
 */
public class FailfastClusterClient extends ClusterClient{

    public FailfastClusterClient(ThreadLocal<LoadBalance<Address>> loadBalance, RpcClient rpcClient, Registry registry) {
        super(loadBalance, rpcClient, registry);
    }

    @Override
    public RpcCommand invokeSync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException, RpcTimeoutException, RpcSendRequestException {
        try {
            Address address = select(targetList);
            RpcCommand result = doInvokeSync(timeoutMillis, address, arg);
            return result;
        } catch (RpcConnectException e) {
            throw e;
        }
    }

    @Override
    public void invokeAsync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException, RpcTimeoutException, RpcSendRequestException {
        try {
            Address address = select(targetList);
            doInvokeSync(timeoutMillis, address, arg);
        } catch (RpcConnectException e) {
            throw e;
        }
    }
}

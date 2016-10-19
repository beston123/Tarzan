package com.tongbanjie.tevent.client.cluster;

import com.tongbanjie.tevent.cluster.Cluster;
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
 * 集群客户端 <p>
 * 支持失败策略和loadBalance
 *
 * @author zixiao
 * @date 16/10/19
 */
public abstract class ClusterClient implements Cluster<RpcCommand, Address, RpcCommand, InvokeCallback> {

    protected ThreadLocal<LoadBalance<Address>> loadBalance;

    protected RpcClient rpcClient;

    public ClusterClient(ThreadLocal<LoadBalance<Address>> loadBalance, RpcClient rpcClient){
        this.loadBalance = loadBalance;
        this.rpcClient = rpcClient;
    }

    protected Address select(List<Address> addressList){
        return loadBalance.get().select(addressList);
    }

    /**
     * oneway方式调用
     * @param timeoutMillis
     * @param retryTimes
     * @param targetList
     * @param arg
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     * @throws RpcTooMuchRequestException
     */
    public void invokeOneway(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException {
        invokeAsync(timeoutMillis, retryTimes, targetList, arg, null);
    }

    /**
     * 同步调用
     * 覆写接口的模版方法，限制throw的Exception范围
     * @param timeoutMillis
     * @param retryTimes
     * @param targetList
     * @param arg
     * @return
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     * @throws RpcTooMuchRequestException
     */
    @Override
    public abstract RpcCommand invokeSync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException;

    /**
     * 异步调用
     * 覆写接口的模版方法，限制throw的Exception范围
     * @param timeoutMillis
     * @param retryTimes
     * @param targetList
     * @param arg
     * @param callback
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     * @throws RpcTooMuchRequestException
     */
    @Override
    public abstract void invokeAsync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException;


    protected RpcCommand doInvokeSync(long timeoutMillis, Address address, RpcCommand request)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException
    {
        if(address == null){
            throw new RpcConnectException("Invoke failed, address is null.");
        }
        return this.rpcClient.invokeSync(address.getAddress(), request, timeoutMillis);
    }

    protected void doInvokeAsync(long timeoutMillis, Address address, RpcCommand request, InvokeCallback invokeCallback)
            throws InterruptedException, RpcConnectException,
            RpcTimeoutException, RpcSendRequestException, RpcTooMuchRequestException
    {
        if(address == null){
            throw new RpcConnectException("Invoke failed, address is null.");
        }
        if(invokeCallback != null){
            this.rpcClient.invokeAsync(address.getAddress(), request, timeoutMillis, invokeCallback);
        }else {
            this.rpcClient.invokeOneway(address.getAddress(), request, timeoutMillis);
        }
    }


}

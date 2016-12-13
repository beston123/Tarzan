package com.tongbanjie.tarzan.client.cluster;

import com.tongbanjie.tarzan.cluster.Cluster;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.cluster.loadbalance.LoadBalance;
import com.tongbanjie.tarzan.registry.Registry;
import com.tongbanjie.tarzan.rpc.InvokeCallback;
import com.tongbanjie.tarzan.rpc.RpcClient;
import com.tongbanjie.tarzan.rpc.exception.RpcConnectException;
import com.tongbanjie.tarzan.rpc.exception.RpcSendRequestException;
import com.tongbanjie.tarzan.rpc.exception.RpcTimeoutException;
import com.tongbanjie.tarzan.rpc.exception.RpcTooMuchRequestException;
import com.tongbanjie.tarzan.rpc.protocol.RpcCommand;

import java.util.List;

/**
 * 集群客户端 <p>
 * 支持失败策略和loadBalance
 *
 * @author zixiao
 * @date 16/10/19
 */
public abstract class ClusterClient implements Cluster<RpcCommand, Address, RpcCommand, InvokeCallback> {

    protected final ThreadLocal<LoadBalance<Address>> loadBalance;

    private final RpcClient rpcClient;

    private final Registry registry;

    private int defaultRetryTimes = 1;

    public ClusterClient(ThreadLocal<LoadBalance<Address>> loadBalance, RpcClient rpcClient, Registry registry){
        this.loadBalance = loadBalance;
        this.rpcClient = rpcClient;
        this.registry = registry;
    }

    protected Address select(List<Address> addressList){
        return loadBalance.get().select(addressList);
    }

    /**
     * 同步调用
     * @param timeoutMillis
     * @param arg
     * @return
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public RpcCommand invokeSync(long timeoutMillis, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException{
        return invokeSync(timeoutMillis, defaultRetryTimes, arg);
    }

    /**
     * 异步调用
     * @param timeoutMillis
     * @param arg
     * @param callback
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public void invokeAsync(long timeoutMillis, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException{
        invokeAsync(timeoutMillis, defaultRetryTimes, arg, callback);
    }

    /**
     * oneway方式调用
     * @param timeoutMillis
     * @param arg
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public void invokeOneWay(long timeoutMillis, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException  {
        invokeOneWay(timeoutMillis, defaultRetryTimes, arg);
    }

    /**
     * 同步调用
     * @param timeoutMillis
     * @param retryTimes
     * @param arg
     * @return
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public RpcCommand invokeSync(long timeoutMillis, int retryTimes, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException{
        List<Address> addressList = this.registry.getDiscovered();
        return invokeSync(timeoutMillis, retryTimes, addressList, arg);
    }

    /**
     * 异步调用
     * @param timeoutMillis
     * @param retryTimes
     * @param arg
     * @param callback
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public void invokeAsync(long timeoutMillis, int retryTimes, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException{
        List<Address> addressList = this.registry.getDiscovered();
        invokeAsync(timeoutMillis, retryTimes, addressList, arg, callback);
    }

    /**
     * oneway方式调用
     * @param timeoutMillis
     * @param retryTimes
     * @param arg
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public void invokeOneWay(long timeoutMillis, int retryTimes, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException  {
        List<Address> addressList = this.registry.getDiscovered();
        invokeOneWay(timeoutMillis, retryTimes, addressList, arg);
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
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    @Override
    public abstract RpcCommand invokeSync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException;

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
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    @Override
    public abstract void invokeAsync(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg, InvokeCallback callback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException;


    /**
     * oneway方式调用
     * @param timeoutMillis
     * @param retryTimes
     * @param targetList
     * @param arg
     * @throws InterruptedException
     * @throws RpcConnectException
     * @throws RpcTooMuchRequestException
     * @throws RpcTimeoutException
     * @throws RpcSendRequestException
     */
    public void invokeOneWay(long timeoutMillis, int retryTimes, List<Address> targetList, RpcCommand arg)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException  {
        invokeAsync(timeoutMillis, retryTimes, targetList, arg, null);
    }


    protected RpcCommand doInvokeSync(long timeoutMillis, Address address, RpcCommand request)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException
    {
        if(address == null){
            throw new RpcConnectException("Invoke failed, address is null.");
        }
        return this.rpcClient.invokeSync(address.getAddress(), request, timeoutMillis);
    }

    protected void doInvokeAsync(long timeoutMillis, Address address, RpcCommand request, InvokeCallback invokeCallback)
            throws InterruptedException, RpcConnectException, RpcTooMuchRequestException,
            RpcTimeoutException, RpcSendRequestException
    {
        if(address == null){
            throw new RpcConnectException("Invoke failed, address is null.");
        }
        if(invokeCallback != null){
            this.rpcClient.invokeAsync(address.getAddress(), request, timeoutMillis, invokeCallback);
        }else {
            this.rpcClient.invokeOneWay(address.getAddress(), request, timeoutMillis);
        }
    }

    @Override
    public Address selectOne(){
        List<Address> addressList = this.registry.getDiscovered();
        return select(addressList);
    }

}

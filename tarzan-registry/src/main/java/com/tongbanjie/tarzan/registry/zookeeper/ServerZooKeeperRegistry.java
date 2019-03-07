package com.tongbanjie.tarzan.registry.zookeeper;

import com.tongbanjie.tarzan.common.exception.TarzanException;
import com.tongbanjie.tarzan.registry.Address;
import com.tongbanjie.tarzan.registry.RegistryType;
import com.tongbanjie.tarzan.registry.ServerAddress;
import com.tongbanjie.tarzan.registry.ServerRegistry;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 服务端zk注册服务 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public class ServerZooKeeperRegistry extends AbstractZooKeeperRegistry implements ServerRegistry{

    //已发现的地址
    protected List<Address> discovered = new ArrayList<Address>();

    private boolean isMaster;

    private int serverId;

    public ServerZooKeeperRegistry(String zkAddress){
        super(RegistryType.SERVER, zkAddress);
        setAsServer();
    }

    private void setAsServer(){
        super.setRegisterRootPath(ZkConstants.SERVERS_ROOT)
                .setRegisterChildPath(ZkConstants.SERVER_CHILD_PATH)
                .setDiscoverPath(ZkConstants.CLIENTS_ROOT);
    }

    @Override
    protected void onDiscoverChanged(List<String> childrenPathList){
        if (CollectionUtils.isEmpty(childrenPathList)) {
            discovered = Collections.EMPTY_LIST;
        }else{
            discovered = toAddressList(childrenPathList);
        }
    }

    private List<Address> toAddressList(List<String> childrenPathList){
        List<Address> addressOnZk = new ArrayList<Address>();

        for(String childPath : childrenPathList){
            Address address = zkClient.readData(this.getDiscoverPath() + ZkConstants.PATH_SEPARATOR + childPath, true);
            if(address != null && address.isEnable()){
                addressOnZk.add(address);
            }
        }
        // 返回不可变List
        return Collections.unmodifiableList(addressOnZk);
    }

    @Override
    public List<Address> getDiscovered() {
        return this.discovered;
    }

    /**
     * 注册服务器
     * 1、占用服务端Id
     * 2、注册服务端地址
     *
     * @param address   服务端地址
     * @param minId     最小服务端Id
     * @param maxId     最大服务端Id
     * @return
     */
    @Override
    public boolean registerServer(Address address, int minId, int maxId){
        //1、占用服务端Id
        this.serverId = tryGetServerId(address, minId, maxId);
        LOGGER.info("Take serverId success, serverId="+serverId);

        //2、注册服务端地址
        ((ServerAddress)address).setServerId(serverId);
        super.register(address);

        //3、订阅server节点变化, 抢占master
        zkClient.subscribeChildChanges(getRegisterRootPath(), new IZkChildListener() {
            /**
             * 用来处理zk server发送过来的通知
             * @param parentPath        对应的父节点的路径
             * @param currentChildren   子节点的相对路径
             * @throws Exception
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                taskMaster(currentChildren);
            }
        });
        taskMaster(zkClient.getChildren(getRegisterRootPath()));
        return true;
    }

    /**
     * 尝试获取服务端Id
     *
     * 尝试在zookeeper中创建名为"serverIds/N"的永久节点(minId<=N<=maxId),
     * 从minId<=N<=maxId，一共尝试X次，
     * a、如果节点不存在，则创建成功后返回Id；
     * b、如果节点已经存在，且节点的ip和端口是当前机器相同，则返回该Id；否则尝试下一个Id；
     * c、如果X次都没成功, 则实例数量已经饱和，抛出异常.
     *
     * @param address
     * @param minId
     * @param maxId
     * @return
     */
    private int tryGetServerId(Address address, int minId, int maxId){
        if(!zkClient.exists(ZkConstants.SERVER_IDS_ROOT)){
            zkClient.createPersistent(ZkConstants.SERVER_IDS_ROOT, true);
        }
        for(int id=minId; id<=maxId; id++){
            String path = ZkConstants.SERVER_IDS_ROOT + ZkConstants.PATH_SEPARATOR + id;
            //a、id没有被使用，则直接占用
            if(!zkClient.exists(path)){
                ((ServerAddress) address).setServerId(id);
                try {
                    zkClient.createPersistent(path, address);
                    return id;
                } catch (ZkNodeExistsException e) {
                    //id has been occupied by another node
                    continue;
                }
            }

            //b、存在且是自身IP，则返回成功
            Address existedAddress = zkClient.readData(path, true);
            if (existedAddress != null && address.getAddress().equals(existedAddress.getAddress())){
                return id;
            }
        }
        throw new TarzanException("Register failed：there is no available server id.");
    }

    /**
     * 抢占master
     * 取最小的serverId作为master
     * TODO 按权重大小weight选择master
     * @param serverPathList
     */
    private void taskMaster(List<String> serverPathList) {
        int minServerId = Integer.MAX_VALUE;
        for(String serverPath : serverPathList){
            ServerAddress serverAddress = zkClient.readData(getRegisterRootPath() + ZkConstants.PATH_SEPARATOR + serverPath, true);
            if(serverAddress != null){
                minServerId = Math.min(minServerId, serverAddress.getServerId());
            }
        }
        isMaster=(serverId == minServerId);
        LOGGER.info("Master selection finished: master id is '{}'. I'm{}the master!", minServerId, isMaster?" ":" not ");
    }

    @Override
    public boolean isMaster() {
        return isMaster;
    }

}

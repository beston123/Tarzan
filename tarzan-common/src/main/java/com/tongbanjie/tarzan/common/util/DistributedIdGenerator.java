package com.tongbanjie.tarzan.common.util;

/**
 * 分布式Id生成器 <p>
 * 生成Long型的递增分布式唯一Id
 *
 * @author zixiao
 * @date 16/10/9
 */
public class DistributedIdGenerator {

    /**
     * 数据中心Id和WorkId， 同一个Jvm只能设置一次
     */
    private static Integer UNIQUE_DATA_CENTER_ID = null;

    private static Integer UNIQUE_WORK_ID = null;

    private DistributedIdGenerator(){}

    public static void initialize(Integer dataCenterId, Integer workId){
        if(UNIQUE_DATA_CENTER_ID == null){
            UNIQUE_DATA_CENTER_ID = dataCenterId;
        }
        if(UNIQUE_WORK_ID == null){
            UNIQUE_WORK_ID = workId;
        }
    }

    public static Long generateId(){
        if(UNIQUE_DATA_CENTER_ID == null || UNIQUE_WORK_ID == null){
            throw new RuntimeException("DataCenterId and workId must specify a value.");
        }
        Long id = IdGeneratorFactory.getInstance().getAndCreate(UNIQUE_DATA_CENTER_ID, UNIQUE_WORK_ID).nextId();
        return id;
    }

    public static int getMinWorkId(){
        return 0;
    }

    public static int getMaxWorkId(){
        return ((Long)IdWorker.getMaxWorkerId()).intValue();
    }

    public static int getMaxDataCenterId(){
        return ((Long)IdWorker.getMaxDataCenterId()).intValue();
    }

    public static boolean validateDataCenterId(long dataCenterId){
        if(dataCenterId < 0 || dataCenterId > IdWorker.getMaxDataCenterId()){
            return true;
        }
        return false;
    }

}

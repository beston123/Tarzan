package com.tongbanjie.tevent.common.util;

import com.tongbanjie.tevent.common.util.IdGeneratorFactory;

/**
 * 分布式Id生成器 <p>
 * 生成Long型的递增分布式唯一Id
 *
 * @author zixiao
 * @date 16/10/9
 */
public class DistributedIdGenerator {

    /**
     * 同一个Jvm只能设置一次
     */
    private static Integer UNIQUE_WORK_ID = null;

    private DistributedIdGenerator(){}

    public static Long generateId(){
        if(UNIQUE_WORK_ID == null){
            throw new RuntimeException("UNIQUE_WORK_ID must specify a value.");
        }
        Long id = IdGeneratorFactory.getInstance().getAndCreate(UNIQUE_WORK_ID).nextId();
        return id;
    }

    public static void setUniqueWorkId(Integer workId){
        if(UNIQUE_WORK_ID == null){
            UNIQUE_WORK_ID = workId;
        }
    }

}

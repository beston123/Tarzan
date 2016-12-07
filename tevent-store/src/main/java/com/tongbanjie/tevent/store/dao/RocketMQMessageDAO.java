package com.tongbanjie.tevent.store.dao;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.store.query.MQMessageQuery;

import java.util.List;

/**
 * RocketMQMessage查询DAO层<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
public interface RocketMQMessageDAO {

    /**
     * 按主键查询
     * @param id
     * @return
     */
    RocketMQMessage selectByPrimaryKey(Long id);

    /**
     * 插入
     * @param record
     * @return
     */
    int insert(RocketMQMessage record);

    /**
     * 按主键更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(RocketMQMessage record);

    /**
     * 按条件分页查询
     * @param query
     * @return
     */
    List<RocketMQMessage> selectByCondition(MQMessageQuery query);

    /**
     * 按条件查询总记录数
     * @param query
     * @return
     */
    int countByCondition(MQMessageQuery query);

    /**
     * 查询最早的记录
     * @return
     */
    List<RocketMQMessage> getEarliest();
}
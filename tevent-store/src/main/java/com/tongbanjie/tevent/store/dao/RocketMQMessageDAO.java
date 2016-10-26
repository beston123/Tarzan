package com.tongbanjie.tevent.store.dao;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.store.PagingParam;
import com.tongbanjie.tevent.store.query.RocketMQMessageQuery;

import java.util.List;

public interface RocketMQMessageDAO {

    RocketMQMessage selectById(Long id);

    int insert(RocketMQMessage record);

    int deleteById(Long id);

    int updateById(RocketMQMessage record);

    List<RocketMQMessage> selectByCondition(RocketMQMessageQuery query);

    List<RocketMQMessage> selectByCondition(RocketMQMessageQuery query, PagingParam pagingParam);

    int countByCondition(RocketMQMessageQuery query);
}
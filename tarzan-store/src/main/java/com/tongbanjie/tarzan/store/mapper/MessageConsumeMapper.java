package com.tongbanjie.tarzan.store.mapper;

import com.tongbanjie.tarzan.store.model.MessageConsume;
import com.tongbanjie.tarzan.store.query.MessageConsumeQuery;

import java.util.List;

public interface MessageConsumeMapper {

    MessageConsume selectByPrimaryKey(Long id);

    int insert(MessageConsume record);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageConsume record);

    List<MessageConsume> selectByCondition(MessageConsumeQuery query);

    int selectCountByCondition(MessageConsumeQuery query);

}

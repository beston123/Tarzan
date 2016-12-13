package com.tongbanjie.tarzan.store.mapper;

import com.tongbanjie.tarzan.store.model.ToSendMessage;
import com.tongbanjie.tarzan.store.query.ToSendMessageQuery;

import java.util.List;

public interface ToSendMessageMapper {

    ToSendMessage selectByPrimaryKey(Long tid);

    int insert(ToSendMessage record);

    int deleteByPrimaryKey(Long tid);

    int updateByPrimaryKeySelective(ToSendMessage record);

    List<ToSendMessage> selectByCondition(ToSendMessageQuery query);

    int selectCountByCondition(ToSendMessageQuery query);

    int incrRetryCount(Long tid);
}
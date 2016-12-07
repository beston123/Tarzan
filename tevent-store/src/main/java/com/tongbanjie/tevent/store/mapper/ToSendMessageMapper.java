package com.tongbanjie.tevent.store.mapper;

import com.tongbanjie.tevent.store.model.ToSendMessage;
import com.tongbanjie.tevent.store.query.ToSendMessageQuery;

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
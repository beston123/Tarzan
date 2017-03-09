package com.tongbanjie.tarzan.store.mapper;

import com.tongbanjie.tarzan.store.model.ToSendMessage;
import com.tongbanjie.tarzan.store.query.ToSendMessageQuery;

import java.util.List;

public interface ToSendMessageMapper {

    ToSendMessage selectByTid(Long tid);

    int insert(ToSendMessage record);

    int deleteByTid(Long tid);

    int updateByTid(ToSendMessage record);

    List<ToSendMessage> selectByCondition(ToSendMessageQuery query);

    int selectCountByCondition(ToSendMessageQuery query);

    int incrRetryCount(Long tid);
}
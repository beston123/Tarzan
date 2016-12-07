package com.tongbanjie.tevent.store.mapper;

import com.tongbanjie.tevent.store.model.ToCheckMessage;
import com.tongbanjie.tevent.store.query.ToCheckMessageQuery;

import java.util.List;

public interface ToCheckMessageMapper {

    ToCheckMessage selectByPrimaryKey(Long tid);

    int insert(ToCheckMessage record);

    int deleteByPrimaryKey(Long tid);

    int updateByPrimaryKeySelective(ToCheckMessage record);

    List<ToCheckMessage> selectByCondition(ToCheckMessageQuery query);

    int selectCountByCondition(ToCheckMessageQuery query);

    int incrRetryCount(Long tid);
}
package com.tongbanjie.tarzan.store.mapper;

import com.tongbanjie.tarzan.store.model.MessageAggregatePlan;
import com.tongbanjie.tarzan.store.query.MessageAggregatePlanQuery;

import java.util.Date;
import java.util.List;

public interface MessageAggregatePlanMapper {

    MessageAggregatePlan selectByPrimaryKey(Integer id);

    int insert(MessageAggregatePlan record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MessageAggregatePlan record);

    List<MessageAggregatePlan> selectByCondition(MessageAggregatePlanQuery query);

    int selectCountByCondition(MessageAggregatePlanQuery query);

    MessageAggregatePlan getLatest(MessageAggregatePlanQuery query);

    Date getNow();

    int deleteByCondition(MessageAggregatePlanQuery query);
}
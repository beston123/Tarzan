package com.tongbanjie.tevent.store.dao;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.PagingParam;
import com.tongbanjie.tevent.store.query.MQMessageQuery;
import com.tongbanjie.tevent.common.util.DistributedIdGenerator;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RocketMQMessage查询DAO层 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
@Repository
public class RocketMQMessageDAOImpl extends ShardingDAO implements RocketMQMessageDAO {

    private final PagingParam defaultPagingParam = new PagingParam(1, 4096);

    @Override
    public RocketMQMessage selectByPrimaryKey(Long id) {
        return getSqlSession().selectOne("RocketMQMessageMapper.selectByPrimaryKey", id);
    }

    @Override
    public int insert(RocketMQMessage record) {
        Long id = DistributedIdGenerator.generateId();
        record.setId(id);
        return getSqlSession().insert("RocketMQMessageMapper.insert", record);
    }


    @Override
    public int updateByPrimaryKeySelective(RocketMQMessage record) {
        return getSqlSession().update("RocketMQMessageMapper.updateByPrimaryKeySelective", record);
    }

    @Override
    public List<RocketMQMessage> selectByCondition(MQMessageQuery query) {
        if(query.getPagingParam() == null){
            query.setPagingParam(defaultPagingParam);
        }
        return getSqlSession().selectList("RocketMQMessageMapper.selectByCondition", query);
    }

    @Override
    public int countByCondition(MQMessageQuery query) {
        return (Integer)getSqlSession().selectOne("RocketMQMessageMapper.countByCondition", query);
    }

    @Override
    public List<RocketMQMessage> getEarliest() {
        return getSqlSession().selectList("RocketMQMessageMapper.getEarliest");
    }


}

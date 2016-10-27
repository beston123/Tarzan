package com.tongbanjie.tevent.store.dao;

import com.tongbanjie.tevent.common.message.RocketMQMessage;
import com.tongbanjie.tevent.common.util.BeanUtils;
import com.tongbanjie.tevent.store.PagingParam;
import com.tongbanjie.tevent.store.query.RocketMQMessageQuery;
import com.tongbanjie.tevent.store.util.DistributedIdGenerator;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
@Repository
public class RocketMQMessageDAOImpl extends BaseDAO implements RocketMQMessageDAO {

    private final static String mapperNameSpace = "RocketMQMessageMapper.";

    private final PagingParam defaultPagingParam = new PagingParam(1, 10000);

    @Override
    public RocketMQMessage selectById(Long id) {
        return getSqlSession().selectOne(mapperNameSpace + "selectByPrimaryKey", id);
    }

    @Override
    public int insert(RocketMQMessage record) {
        Long id = DistributedIdGenerator.generateId();
        record.setId(id);
        return getSqlSession().insert(mapperNameSpace + "insert", record);
    }

    @Override
    public int deleteById(Long id) {
        return getSqlSession().delete(mapperNameSpace + "deleteByPrimaryKey", id);
    }

    @Override
    public int updateById(RocketMQMessage record) {
        return getSqlSession().update(mapperNameSpace + "updateByPrimaryKeySelective", record);
    }

    @Override
    public List<RocketMQMessage> selectByCondition(RocketMQMessageQuery query) {
        return selectByCondition(query, defaultPagingParam);
    }

    @Override
    public List<RocketMQMessage> selectByCondition(RocketMQMessageQuery query, PagingParam pagingParam) {
        Map<String, Object> params = BeanUtils.beanToMap(query);
        params.put("pagingParam", pagingParam);
        return getSqlSession().selectList(mapperNameSpace + "selectByCondition", params);
    }

    @Override
    public int countByCondition(RocketMQMessageQuery query) {
        return (Integer)getSqlSession().selectOne(mapperNameSpace + "countByCondition", query);
    }
}

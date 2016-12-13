package com.tongbanjie.tarzan.store.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 分表分库 查询层 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */
@Repository
public class ShardingDAO extends SqlSessionDaoSupport {

    @Resource(name = "shardingSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

}
package com.tongbanjie.tevent.common.redis;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.SafeEncoder;

import javax.annotation.PreDestroy;
import java.util.*;

/**
 * 〈Redis组件〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/6
 */
public class RedisComponent {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Long LOCK_EXPIRED_TIME = 5 * 1000L;// 分布式锁的失效时间：5秒

    private ShardedJedisPool pool;

    private Serializer serializer;

    /****************** 常用方法 *******************/

    /**
     * 获取一个锁
     *
     * @param lock
     * @return
     */
    public boolean acquireLock(String lock) {
        return acquireLock(lock, LOCK_EXPIRED_TIME);
    }


    /**
     * 获取一个锁，自旋等待参数tryTime规定时间
     * @param lock
     * @param expired 锁的失效时间（毫秒）
     * @param tryTime 获取锁等待时间（毫秒）
     * @return
     */
    public boolean acquireLock(String lock, long expired, int tryTime){
        long beginTime = System.currentTimeMillis();
        boolean lockFlag = acquireLock(lock, expired);

        while (!lockFlag) {
            lockFlag = acquireLock(lock, expired);
            if ((System.currentTimeMillis() - beginTime) >= tryTime) {
                // 等待超时，
                return lockFlag;
            }
        }
        return true;
    }

    /**
     * 获取一个锁 必须保证分布式环境的多个主机的时钟是一致的
     *
     * @param lockKey
     * @expired 锁的失效时间（毫秒）
     * @return
     */
    public boolean acquireLock(String lockKey, long expired) {
        ShardedJedis jedis = null;
        boolean success = false;
        try {
            jedis = pool.getResource();
            long value = System.currentTimeMillis() + expired + 1;
            // 通过setnx获取一个lock
            long acquired = jedis.setnx(lockKey, String.valueOf(value));
            if (acquired == 1) {
                // setnx成功，则成功获取一个锁
                success = true;
            } else {
                // setnx失败，说明锁仍然被其他对象保持，检查其是否已经超时
                long oldValue = Long.valueOf(jedis.get(lockKey));
                if (oldValue < System.currentTimeMillis()) {
                    // 超时
                    String getValue = jedis.getSet(lockKey, String.valueOf(value));
                    // 获取锁成功
                    if (Long.valueOf(getValue) == oldValue) {
                        success = true;
                    } else {
                        // 已被其他进程捷足先登了
                        success = false;
                    }
                } else {
                    // 未超时，则直接返回失败
                    success = false;
                }
            }
        } catch (Throwable e) {
            LOGGER.error("acquireLock error", e);
        } finally {
            returnResource(jedis);
        }
        return success;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     *            key
     */
    public void releaseLock(String lockKey) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            long current = System.currentTimeMillis();
            // 避免删除非自己获取到的锁
            if (jedis.get(lockKey) != null && current < Long.valueOf(jedis.get(lockKey))) {
                jedis.del(lockKey);
            }
        } catch (Throwable e) {
            LOGGER.error("releaseLock error", e);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取 key-value 的 value
     */
    public <T> T get(final String key, final Class<T> c) {
        return this.execute(new JedisAction<T>() {
            @Override
            public T action(ShardedJedis jedis) {
                byte[] bs = jedis.get(SafeEncoder.encode(key));
                return deserialization(bs, c);
            }
        });
    }

    /**
     * 获取 key-value 的 value. <br>
     * 如果 value 是一个 list, 请使用此方法.
     */
    public <T> List<T> getList(final String key, final Class<T> c) {
        return this.executeForList(new JedisActionForList<T>() {
            @Override
            public List<T> action(ShardedJedis jedis) {
                byte[] bs = jedis.get(SafeEncoder.encode(key));
                return deserializationList(bs, c);
            }
        });
    }

    /**
     * 缓存 key-value
     */
    public void set(final String key, final Object value) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                jedis.set(SafeEncoder.encode(key), serialzation(value));
            }
        });
    }

    /**
     * 缓存 key-value , seconds 过期时间,单位为秒.
     */
    public void set(final String key, final Object value, final int seconds) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                jedis.setex(SafeEncoder.encode(key), seconds, serialzation(value));
            }
        });
    }

    /**
     * 获取 key mapKey mapValue 中的 mapValue 列表.
     */
    public <T> List<T> hvals(final String key, final Class<T> c) {
        return this.executeForList(new JedisActionForList<T>() {
            @Override
            public List<T> action(ShardedJedis jedis) {
                Collection<byte[]> value = jedis.hvals(SafeEncoder.encode(key));
                List<T> list = new ArrayList<T>(value.size());
                for (byte[] bs : value) {
                    list.add(deserialization(bs, c));
                }
                return list;
            }
        });
    }

    /**
     * 获取 key mapKey mapValue 中指定的 mapValue.
     */
    public <T> T hget(final String key, final Object mapKey, final Class<T> c) {
        return this.execute(new JedisAction<T>() {
            @Override
            public T action(ShardedJedis jedis) {
                byte[] bs = jedis.hget(SafeEncoder.encode(key), serialzation(mapKey));
                return deserialization(bs, c);
            }
        });
    }

    /**
     * 获取 key mapKey mapValue 中指定的 mapValue.<br>
     * 如果 mapValue 是一个 list, 请使用此方法.
     */
    public <T> List<T> hgetList(final String key, final Object mapKey, final Class<T> c) {
        return this.executeForList(new JedisActionForList<T>() {
            @Override
            public List<T> action(ShardedJedis jedis) {
                byte[] value = jedis.hget(SafeEncoder.encode(key), serialzation(mapKey));
                return deserializationList(value, c);
            }
        });
    }

    /**
     * 缓存 key mapKey mapValue.
     */
    public void hset(final String key, final Object mapKey, final Object mapValue) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                jedis.hset(SafeEncoder.encode(key), serialzation(mapKey), serialzation(mapValue));
            }
        });
    }

    public void hset(final String key, final Object mapKey, final Object mapValue, final int second) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                jedis.hset(SafeEncoder.encode(key), serialzation(mapKey), serialzation(mapValue));
                jedis.expire(key, second);
            }
        });
    }

    /**
     * 删除集合中对应的key/value
     */
    public void hdel(final String key, final Object mapKey) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                jedis.hdel(SafeEncoder.encode(key), serialzation(mapKey));
            }
        });
    }

    /**
     * 缓存 key map<mapKey,mapValue>.
     */
    public void hmset(final String key, final Map<Object, Object> map) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                if (MapUtils.isNotEmpty(map)) {
                    Map<byte[], byte[]> m = new HashMap<byte[], byte[]>(map.size());

                    Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Object, Object> next = it.next();
                        m.put(serialzation(next.getKey()), serialzation(next.getValue()));
                    }
                    jedis.hmset(SafeEncoder.encode(key), m);
                }
            }
        });
    }

    /**
     * 缓存 key map<mapKey,mapValue>，expireSeconds秒时间失效
     */
    public void hmset(final String key, final Map<Object, Object> map, final int expireSeconds) {
        this.execute(new JedisActionNoResult() {
            @Override
            public void action(ShardedJedis jedis) {
                if (MapUtils.isNotEmpty(map)) {
                    Map<byte[], byte[]> m = new HashMap<byte[], byte[]>(map.size());

                    Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Object, Object> next = it.next();
                        m.put(serialzation(next.getKey()), serialzation(next.getValue()));
                    }
                    jedis.hmset(SafeEncoder.encode(key), m);
                    jedis.expire(key, expireSeconds);
                }
            }
        });
    }

    /**
     * 删除一个 Key.
     */
    public Long del(final String key) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.del(key);
            }
        });
    }

    /**
     * redis zadd command.
     */
    public Long zadd(final String key, final double score, final Object member) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.zadd(SafeEncoder.encode(key), score, serialzation(member));
            }
        });
    }

    /**
     * redis zrange command.
     */
    public <T> List<T> zrange(final String key, final long start, final long end, final Class<T> clazz) {
        return this.executeForList(new JedisActionForList<T>() {
            @Override
            public List<T> action(ShardedJedis jedis) {
                Collection<byte[]> value = jedis.zrange(SafeEncoder.encode(key), start, end);
                List<T> list = new ArrayList<T>(value.size());
                for (byte[] b : value) {
                    list.add(deserialization(b, clazz));
                }
                return list;
            }
        });
    }

    /**
     * redis zrangeByScore command.
     */
    public <T> List<T> zrangeByScore(final String key, final double min, final double max, final Class<T> clazz) {
        return this.executeForList(new JedisActionForList<T>() {
            @Override
            public List<T> action(ShardedJedis jedis) {
                Collection<byte[]> value = jedis.zrangeByScore(SafeEncoder.encode(key), min, max);
                List<T> list = new ArrayList<T>(value.size());
                for (byte[] b : value) {
                    list.add(deserialization(b, clazz));
                }
                return list;
            }
        });
    }

    /**
     * redis zremrangeByScore command.
     */
    public Long zremrangeByScore(final String key, final double start, final double end) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.zremrangeByScore(key, start, end);
            }
        });
    }

    public Long zremrange(final String key, final String... members) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.zrem(key, members);
            }
        });
    }

    /**
     * redis incr command.
     */
    public Long incr(final String key) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    /**
     * redis incrby command.
     */
    public Long incrBy(final String key, final long integer) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.incrBy(key, integer);
            }
        });
    }

    /**
     * redis decr command.
     */
    public Long decr(final String key) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    /**
     * redis decrby command.
     */
    public Long decrBy(final String key, final long integer) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.decrBy(key, integer);
            }
        });
    }

    /**
     * redis expire command.
     */
    public Long expire(final String key, final int seconds) {
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    public Long sadd(final String key, final Object value){
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.sadd(SafeEncoder.encode(key), serialzation(value));
            }
        });
    }

    public Long srem(final String key, final Object value){
        return this.execute(new JedisAction<Long>() {
            @Override
            public Long action(ShardedJedis jedis) {
                return jedis.srem(SafeEncoder.encode(key), serialzation(value));
            }
        });
    }

    public Set<String> smembers(final String key){
        return this.execute(new JedisAction<Set<String>>() {
            @Override
            public Set<String> action(ShardedJedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    // internal method
    // -----------------------------------------------------------------------

    /**
     * 执行有返回结果的action。
     */
    public <T> T execute(JedisAction<T> jedisAction) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedisAction.action(jedis);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 执行有返回结果,并且返回结果是List的action。
     */
    public <T> List<T> executeForList(JedisActionForList<T> jedisAction) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedisAction.action(jedis);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 执行无返回结果的action。
     */
    public void execute(JedisActionNoResult jedisAction) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedisAction.action(jedis);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 有返回结果的回调接口定义。
     */
    public interface JedisAction<T> {
        T action(ShardedJedis jedis);
    }

    /**
     * 有返回结果的回调接口定义。
     */
    public interface JedisActionForList<T> {
        List<T> action(ShardedJedis jedis);
    }

    /**
     * 无返回结果的回调接口定义。
     */
    public interface JedisActionNoResult {
        void action(ShardedJedis jedis);
    }

    // private method
    // -----------------------------------------------------------------------
    private byte[] serialzation(Object object) {
        return serializer.serialzation(object);
    }

    private <T> T deserialization(byte[] byteArray, Class<T> c) {
        return serializer.deserialization(byteArray, c);
    }

    private <E> List<E> deserializationList(byte[] byteArray, Class<E> elementC) {
        return serializer.deserializationList(byteArray, elementC);
    }

    private void returnResource(ShardedJedis jedis) {
        // 返还到连接池
        if (jedis != null) {
            try {
                pool.returnResource(jedis);
            } catch (Throwable e) {
                returnBrokenResource(jedis);
            }
        }
    }

    private void returnBrokenResource(ShardedJedis jedis) {
        if (jedis != null) {
            try {
                pool.returnBrokenResource(jedis);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    @PreDestroy
    public void destory() {
        try {
            pool.destroy();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    public static interface Serializer {
        byte[] serialzation(Object object);

        <T> T deserialization(byte[] byteArray, Class<T> c);

        <E> List<E> deserializationList(byte[] byteArray, Class<E> elementC);
    }

    public ShardedJedisPool getPool() {
        return pool;
    }

    public void setPool(ShardedJedisPool pool) {
        this.pool = pool;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public void init() throws Exception {
        if(this.serializer == null){
            // 为了向下兼容默认,如果没有提供序列化器,默认使用,json序列化
            serializer = new JsonSerializer();
        }
        LOGGER.info("RedisComponent [" + this.toString() + "] is done! serializer:" + serializer.toString());
    }

}
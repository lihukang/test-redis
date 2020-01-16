package com.nhncorp.ooms.redis.core;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Response;


/**
 * 
 * @ClassName   : RedisStringManager.java
 * @Description : 
 * @author Yin Xueyuan
 * @since 2018年4月13日
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018年4月13日        Yin Xueyuan           fisrt create
 * </pre>
 */
public interface RedisZSetManager<K,V> extends RedisManager<K,V>{
	
	/**
	 * 添加集合
	 * @param key
	 * @param value
	 */
	Long zadd(K key,V value);
	
	/**
	 * 添加集合
	 * @param key
	 * @param value
	 */
	Long zaddByScore(K key,double score,V value);
	
	/**
	 * 批量添加集合
	 * @param key
	 * @param value
	 */
	Response<Long> zaddByScoreByPipeLine(K key,double score,List<Object> list);
	
	/**
	 * 取合集
	 * @param key
	 * @return
	 */
	Long zcard(K key);
	
	/**
	 * 取交集
	 * @param key
	 * @param value
	 * @return
	 */
	Long zinterstor(K key, V value);
	
	/**
	 * 移除元素
	 * @param key
	 * @return
	 */
	Long zrem(K key);
	
	/**
	 * 获取集合
	 * @param key
	 * @param first
	 * @param last
	 * @return
	 */
	Set<V>  zrange(K key, long start, long end);
	
	/**
	 * 按score删除
	 * @param key
	 * @param first
	 * @param last
	 * @return
	 */
	Long  zremrangeByScore(K key, long start, long end);
	
	/**
	 * 获取集合
	 * @param key
	 * @param first
	 * @param last
	 * @return
	 */
	Set<V> zrangeByScore(K key,double first,double last);
	

}

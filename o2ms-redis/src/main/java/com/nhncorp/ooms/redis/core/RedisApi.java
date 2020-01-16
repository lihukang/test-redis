package com.nhncorp.ooms.redis.core;

import java.util.Set;

import com.nhncorp.ooms.redis.util.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;


/**
 * 
 * @ClassName   : RedisApi.java
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
public class RedisApi<K,V> {
	
	private RedisFactory redisFactory;
	
	private RedisStringManager<K,V> redisStringManager;
	
	private RedisZSetManager<K,V> redisZSetManager;

	public RedisFactory getRedisFactory() {
		return redisFactory;
	}

	public void setRedisFactory(RedisFactory redisFactory) {
		this.redisFactory = redisFactory;
	}
	
	public Jedis getJedis(){
		return redisFactory.getPool().getResource();
	}
	
	public Pipeline getPipeLine(){
		return redisFactory.getPool().getResource().pipelined();
	}
	
	public void setString(K key,V value){
		getRedisStringManager().set(key, value);
	}
	
	public V getString(K key){
		return getRedisStringManager().get(key);
	}
	
	public void updateString(K key,V value){
		getRedisStringManager().update(key, value);
	}
	
	public Long zadd(K key,V value){
		return getRedisZSetManager().zadd(key, value);
	}
	
	public void zaddByScore(K key,double score,V value){
			getRedisZSetManager().zaddByScore(key,score, value);
	}
	
	public Set<V>  zrange(K key,long start, long end){
		return getRedisZSetManager().zrange(key, start,end);
	}
	
	public Long  zremrangeByScore(K key,long start, long end){
		return getRedisZSetManager().zremrangeByScore(key, start,end);
	}

	public Set<V>  zrangeByScore(K key,long start, long end){
		return getRedisZSetManager().zrangeByScore(key, start,end);
	}
	
	private RedisStringManager<K,V>  getRedisStringManager(){
		if (redisStringManager == null) {
			redisStringManager = new CommonRedisStringManager<K, V>(this);
		}
		return redisStringManager;
	}
	
	private RedisZSetManager<K,V>  getRedisZSetManager(){
		if (redisZSetManager == null) {
			redisZSetManager = new CommonRedisZSetManager<K, V>(this);
		}
		return redisZSetManager;
	}
	
	@SuppressWarnings("unchecked")
	public Long delete(K... keys) {
		final byte[][] rawKeys = new byte[keys.length][];

		int i = 0;
		for (K key : keys) {
			rawKeys[i++] = SerializeUtil.writeToByteArray(key);
		}
		return getJedis().del(rawKeys);
	}
	
	public void flushDb(){
		getJedis().flushAll();
	}

}

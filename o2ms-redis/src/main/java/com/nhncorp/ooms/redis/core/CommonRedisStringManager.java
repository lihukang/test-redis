package com.nhncorp.ooms.redis.core;

import redis.clients.jedis.Jedis;

import com.nhncorp.ooms.redis.util.SerializeUtil;

/**
 * 
 * @ClassName   : CommonRedisStringManager.java
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
public class CommonRedisStringManager<K,V> implements RedisStringManager<K, V> {

	RedisApi<K, V> redisApi;
	
	public CommonRedisStringManager(RedisApi<K, V> redisApi){
		this.redisApi = redisApi;
	}
	
	@Override
	public void set(K key, V value) {
		
		Jedis jedis = null;
		try {
		    jedis = redisApi.getJedis();
		    jedis.set(SerializeUtil.writeToByteArray(key), SerializeUtil.writeToByteArray(value));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jedis.close();
		}
		
	}

	@Override
	public V get(K key) {
		V value = null;
		if(exits(key)){
		  value= SerializeUtil.readFromByteArray(redisApi.getJedis().get(SerializeUtil.writeToByteArray(key)));
		}
		return value;
	}


	@Override
	public V update(K key, V value) {
		return SerializeUtil.readFromByteArray(redisApi.getJedis().getSet(SerializeUtil.writeToByteArray(key), SerializeUtil.writeToByteArray(value)));
	}

	@Override
	public boolean exits(K key) {
		return redisApi.getJedis().exists(SerializeUtil.writeToByteArray(key));
	}



}

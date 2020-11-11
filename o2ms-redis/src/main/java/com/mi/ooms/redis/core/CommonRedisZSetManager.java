package com.mi.ooms.redis.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.mi.ooms.redis.util.SerializeUtil;

/**
 * 
 * @ClassName   : CommonRedisStringManager.java
 * @Description : 
 * @author Lee Hukang
 * @since 2018年4月13日
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018年4月13日        Lee Hukang           fisrt create
 * </pre>
 */
public class CommonRedisZSetManager<K,V> implements RedisZSetManager<K, V> {

	RedisApi<K, V> redisApi;
	
	public CommonRedisZSetManager(RedisApi<K, V> redisApi){
		this.redisApi = redisApi;
	}
	
	
	@Override
	public boolean exits(K key) {
		return redisApi.getJedis().exists(SerializeUtil.writeToByteArray(key));
	}


	@Override
	public Long zadd(K key, V value) {
		return redisApi.getJedis().zadd(SerializeUtil.writeToByteArray(key),12334, SerializeUtil.writeToByteArray(value));
		
	}


	@Override
	public Long zaddByScore(K key, double score, V value) {
		Jedis jedis = null;
		Long l = null;
		try {
		    jedis = redisApi.getJedis();
		    l = jedis.zadd(SerializeUtil.writeToByteArray(key),score, SerializeUtil.writeToByteArray(value));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jedis.close();
		}
		return l;
		
	}
	
	@Override
	public Response<Long>  zaddByScoreByPipeLine(K key, double score, List<Object> list) {
		Jedis jedis = null;
		Pipeline pipe = null;
		Response<Long> res = null;
		try {
			jedis = redisApi.getJedis();
			pipe = jedis.pipelined();
			for(Object obj : list){
				res = pipe.zadd(SerializeUtil.writeToByteArray(key),score, SerializeUtil.writeToByteArray(obj));
			}
			pipe.sync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jedis.close();
			try {
				pipe.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
		
	}


	@Override
	public Long zcard(K key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zinterstor(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long zrem(K key) {
		// TODO Auto-generated method stub
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Set<V> zrange(K key, long start, long end) {
		Set<V> set = new HashSet<V>();
		
		Set<byte[]> results =  redisApi.getJedis().zrange(SerializeUtil.writeToByteArray(key), start, end);
		Iterator<byte[]> it = (Iterator<byte[]>) results.iterator();  
		while (it.hasNext()) {  
		  byte[] bt = it.next();  
		  System.out.println("zlist"+SerializeUtil.readFromByteArray(bt));
		  set.add((V)SerializeUtil.readFromByteArray(bt));
		} 
		
		return set;
	}
	
	@Override
	public Long zremrangeByScore(K key, long start, long end) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = redisApi.getJedis();
			result = jedis.zremrangeByScore(SerializeUtil.writeToByteArray(key), start, end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jedis.close();
		}
		
		return result;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Set<V> zrangeByScore(K key, double start, double end) {
		Set<V> set = new HashSet<V>();
		Jedis jedis = null;
		
		Set<byte[]> results = null;
		try {
			jedis = redisApi.getJedis();
			results = jedis.zrangeByScore(SerializeUtil.writeToByteArray(key), start, end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jedis.close();
		}
		
		Iterator<byte[]> it = (Iterator<byte[]>) results.iterator();  
		while (it.hasNext()) {  
		  byte[] bt = it.next();  
		  set.add((V)SerializeUtil.readFromByteArray(bt));
		} 
		
		return set;
	}



}

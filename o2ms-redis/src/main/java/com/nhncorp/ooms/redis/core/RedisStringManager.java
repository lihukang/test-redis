package com.nhncorp.ooms.redis.core;


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
public interface RedisStringManager<K,V> extends RedisManager<K,V>{
	
	void set(K key,V value);
	
	V get(K key);
	
	V update(K key, V value);
	

}

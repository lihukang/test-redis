package com.nhncorp.ooms.redis.core;

/**
 * 
 * @ClassName   : RedisManager.java
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
public interface RedisManager<K,V> {
	
	 boolean exits(K key);

}

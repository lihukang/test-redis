package com.mi.ooms.redis.core;

/**
 * 
 * @ClassName   : RedisManager.java
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
public interface RedisManager<K,V> {
	
	 boolean exits(K key);

}

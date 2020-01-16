package com.nhncorp.ooms.redis.conf;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import redis.clients.jedis.JedisPoolConfig;

import com.nhncorp.ooms.redis.core.RedisApi;
import com.nhncorp.ooms.redis.core.RedisFactory;

/**
 * 
 * @ClassName   : RedisConf.java
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
@Configuration
public class RedisConf extends WebMvcConfigurerAdapter{
	
	  	@Value("${redis.host}")
	    private String host;

	    @Value("${redis.port}")
	    private int port;

	    @Value("${redis.timeout}")
	    private int timeout;

	    @Value("${redis.pool.max-idle}")
	    private int maxIdle;

	    @Value("${redis.pool.min-idle}") 
	    private int minIdle;
	    
	
	  /**
	   * redis工厂
	   * @Description:
	   * @return
	   */
	  @Bean
	  public RedisFactory redisFactory() {

		RedisFactory factory = new RedisFactory();
	    factory.setHostName(host);
	    factory.setPort(port);
	    //设置连接超时时间
	    factory.setTimeout(timeout); 
	    factory.setUsePool(true);
	    factory.setPoolConfig(jedisPoolConfig());
	    return factory;
	  }
	  
	  
	  /**
	   * redis连接池
	   * @Description:
	   * @return
	   */
	  @Bean
	  public JedisPoolConfig jedisPoolConfig() {
	    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	    jedisPoolConfig.setMaxIdle(maxIdle);
	    jedisPoolConfig.setMinIdle(minIdle);
	    jedisPoolConfig.setMaxTotal(1000);
	    return jedisPoolConfig;
	  }
	  
	  
	  /**
	   * redisApi
	   * @Description:
	   * @param factory
	   * @return
	   */
	  @Bean
	  public RedisApi<String,Object> redisApi() {
		  RedisApi<String,Object> redisApi = new RedisApi<String,Object>();
		  redisApi.setRedisFactory(redisFactory());
	      return redisApi; 
	  }
	  
	  @Bean
	  public HttpMessageConverter<String> responseBodyConverter() {
	      StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
	      return converter;
	  }

	  @Override
	  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	      super.configureMessageConverters(converters);
	      converters.add(responseBodyConverter());
	  }
	  
	  @Bean
	    public EmbeddedServletContainerFactory servletContainer() {
	        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	        tomcat.setUriEncoding(Charset.forName("UTF-8"));
	        return tomcat;
	    }
	  
}

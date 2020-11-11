package com.mi.ooms.redis.conf;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.mi.ooms.redis.core.RedisApi;
import com.mi.ooms.redis.core.RedisFactory;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @ClassName : RedisConf.java
 * @Description :
 * @author Lee Hukang
 * @since 2018年4月13日
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018年4月13日        Lee Hukang           fisrt create
 *               </pre>
 */
@Configuration
public class RedisConf extends WebMvcConfigurationSupport {

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
	 * 
	 * @Description:
	 * @return
	 */
	@Bean
	public RedisFactory redisFactory() {

		RedisFactory factory = new RedisFactory();
		factory.setHostName(host);
		factory.setPort(port);
		// 设置连接超时时间
		factory.setTimeout(timeout);
		factory.setUsePool(true);
		factory.setPoolConfig(jedisPoolConfig());
		return factory;
	}

	/**
	 * redis连接池
	 * 
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
	 * 
	 * @Description:
	 * @param factory
	 * @return
	 */
	@Bean
	public RedisApi<String, Object> redisApi() {
		RedisApi<String, Object> redisApi = new RedisApi<String, Object>();
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
	public TomcatServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint constraint = new SecurityConstraint();
				constraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				constraint.addCollection(collection);
				context.addConstraint(constraint);
			}
		};
		tomcat.setUriEncoding(Charset.forName("UTF-8"));
		tomcat.addAdditionalTomcatConnectors(httpConnector());
		return tomcat;
	}

	@Bean
	public Connector httpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		// Connector监听的http的端口号
		connector.setPort(9090);
		connector.setSecure(false);
		// 监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(9090);
		return connector;
	}

}

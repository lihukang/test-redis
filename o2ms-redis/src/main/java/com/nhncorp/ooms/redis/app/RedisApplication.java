package com.nhncorp.ooms.redis.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan(basePackages = "com.nhncorp.ooms")
@ImportResource(value = {"classpath*:/spring/context-mapper.xml"})
public class RedisApplication{
	
	
    public static void main(String[] args) {
    	new SpringApplicationBuilder(RedisApplication.class).web(true).run(args);
    	
    }
    
}

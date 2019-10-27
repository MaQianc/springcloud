/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.chuanglan.mongo.service.config;

import java.util.concurrent.Executor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.chuanglan.advert.common.redis.RedisKeyUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Bomb create-time 2019-01-05 14:56:41
 */
@Slf4j
@Configuration
@EnableAsync
//@EnableMongoRepositories(basePackages="com.chuanglan.mongo.service.repository",createIndexesForQueryMethods=true)
public class AppConfiguration implements AsyncConfigurer{
	
	@Value("${spring.application.name}")
	private String appName;
	
	@Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(MongoCustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        	log.error("mappingConverter setting failed",ignore);
        }
        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }
	
	@Bean
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {  
        return new MongoTransactionManager(dbFactory);
    }

	/**
	 * define validator bean.
	 * 
	 * @return Validator
	 */
	@Bean
	public Validator getValidator() {
		/*LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		// validator.setValidationMessageSource(getMessageSource());
		return validator;*/
		ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
	            .configure()
	            .addProperty( "hibernate.validator.fail_fast", "true" ) //为true时代表快速失败模式，false则为全部校验后再结束。
	            .buildValidatorFactory();
	    Validator validator = validatorFactory.getValidator();

	    return validator;
	}
	
	@Override
	@Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("daily-statis-");
        executor.initialize();
        return executor;
    }
	
	@Bean
	public RedisKeyUtils getRedisKeyUtils() {
		RedisKeyUtils keyUtils =  new RedisKeyUtils();
		keyUtils.setAppName(appName);
		return keyUtils;
	}

}

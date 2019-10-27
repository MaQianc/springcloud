package com.mq.springcloud.consul.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mq.springcloud.consul.demo.annotation.ExcludeFromComponentScan;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;

@Configuration
@ExcludeFromComponentScan  //如果配置类放在启动类包下 需要定义注解排除
public class RibbonConfig {

	@Autowired
	IClientConfig config;
	
	/**
     * 设置负载均衡的规则为随机
     * */
	@Bean
	public IRule MyRibbonRule() {
		System.out.println("随机的....");
		return new RandomRule(); //RandomRule RetryRule RoundRibinRule
	}
}

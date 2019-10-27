package com.mq.springcloud.consul.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

import com.mq.springcloud.consul.demo.annotation.ExcludeFromComponentScan;
import com.mq.springcloud.consul.demo.config.RibbonConfig;

@SpringBootApplication
@EnableDiscoveryClient  //单纯的消费（不对外提供服务）可以不注册
@RibbonClient(name = "service-producer", configuration = RibbonConfig.class)
//@RibbonClient(name = "spring-cloud-consul-producer", configuration = RibbonConfig.class)
@ComponentScan(excludeFilters =
{@ComponentScan.Filter(type= FilterType.ANNOTATION, value=ExcludeFromComponentScan.class)}
		)
public class SpringCloudConsulConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConsulConsumerApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

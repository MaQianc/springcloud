package com.mq.springcloud.consul.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  //单纯的消费（不对外提供服务）可以不注册
public class SpringCloudConsulConsumerApplication {

	public static void main(String[] args) {
		System.out.print("123258");
		SpringApplication.run(SpringCloudConsulConsumerApplication.class, args);
	}

}

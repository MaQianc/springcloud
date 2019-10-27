package com.mq.springcloud.consul.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CallHelloController {
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @RequestMapping("/call")
    public String call(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-producer");
        System.out.println("服务地址：" + serviceInstance.getUri());
        System.out.println("服务名称：" + serviceInstance.getServiceId());

        String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello", String.class);
        System.out.println(callServiceResult);
        return callServiceResult;
    }
    
    @RequestMapping("/getPathParam/{user}")
    public String getPathParam(@PathVariable("user") String username){
    	ServiceInstance serviceInstance = loadBalancerClient.choose("service-producer");
    	System.out.println("服务地址：" + serviceInstance.getUri());
    	System.out.println("服务名称：" + serviceInstance.getServiceId());
    	Map<String, String> params = new HashMap<>();
    	params.put("user", username);
    	//String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello", String.class);
    	RestTemplate restTemplate = new RestTemplate();
    	String paramServiceResult = restTemplate.getForObject(serviceInstance.getUri().toString() + "/hello/{user}", String.class,params);
    	System.out.println(paramServiceResult);
    	return paramServiceResult;
    }
    @RequestMapping("/getPathParam1/{user}")
    public String getPathParam2(@PathVariable("user") String username){
    	ServiceInstance serviceInstance = loadBalancerClient.choose("service-producer");
    	System.out.println("服务地址：" + serviceInstance.getUri());
    	System.out.println("服务名称：" + serviceInstance.getServiceId());
    	
    	//String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello", String.class);
    	String paramServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello1/{user}", String.class,username);
    	System.out.println(paramServiceResult);
    	return paramServiceResult;
    }
    
}

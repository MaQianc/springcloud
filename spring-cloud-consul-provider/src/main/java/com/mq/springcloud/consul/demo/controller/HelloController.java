package com.mq.springcloud.consul.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public String getHello() {
		return "hello";
	}
	
	@GetMapping("/hello/{user}")
	public String getPathParams(@PathVariable("user") String user) {
		return user;
	}
	@GetMapping("/hello1/{user}")
	public String getPathParams1(@PathVariable("user") String user) {
		return user;
	}
}

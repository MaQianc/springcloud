/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.type;

/**
 * @author      Bomb
 * create-time  2019-01-16 16:04:08
 */
public enum MessageStatusType {
	/**
	 * 初始化位置状态
	 */
	UNKOWN("unkown",0),
	/**
	 * 发送成功
	 */
	SUCCESS("success",1),
	/**
	 * 发送失败
	 */
	FAILED("failed",2),
	
	/**
	   *  获取广告超时
	 */
	TIME_OUT("timeout",3);
	/**
	 * private constructor.
	 * @param name
	 * @param value
	 */
	private MessageStatusType(String name,Integer value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * status name
	 */
	private String name;
	
	/**
	 * status stored key
	 */
	private Integer value;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}
	
	
}

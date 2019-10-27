/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.type;

/**
 * @author      Bomb
 * create-time  2019-02-28 14:05:39
 */
public enum DynamicStaitsQueryType {

	TIME("TIME",0),
	TEMPLATE("template",1),
	ADVERTISER("advertiser",2),
	FLUX("flux",3);
	
	/**
	 * 类型名称
	 */
	private String name;
	
	/**
	 * 类型
	 */
	private Integer type;
	
	private DynamicStaitsQueryType(String name,Integer type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	
	
}

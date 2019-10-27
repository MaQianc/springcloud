/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.type;

/**
 * @author      Bomb
 * create-time  2019-01-10 11:25:21
 */
public enum CollectionType {

	/**
	 * 流量主
	 */
	FLUX(1),
	/**
	 * 广告主
	 */
	ADVERTER(0);
	
	private Integer value;
	
	private CollectionType(Integer value) {
		this.value=value;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}
	
	
}

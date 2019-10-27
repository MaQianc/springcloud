/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.util;

import org.springframework.stereotype.Component;

/**
 * @author      Bomb
 * create-time  2019-01-17 11:38:05
 */
@Component("threadLocalContainerUtil")
public class ThreadLocalContainerUtil {

	private static final ThreadLocal<String> messageDocudmentNameLocal = new ThreadLocal<>();
	
	public  void putDocumentName(String messsageDocudmentName) {
		messageDocudmentNameLocal.set(messsageDocudmentName);
	}
	
	public  String getDocumentName() {
		return messageDocudmentNameLocal.get() != null ? messageDocudmentNameLocal.get() : "common_message";
	}
	
	public  void clearDocudmentName() {
		messageDocudmentNameLocal.remove();
	}
}

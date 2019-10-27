/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.constant;

import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

/**
 * project constants
 * @author      Bomb
 * create-time  2019-01-14 15:18:48
 */
public class Constants {
	
	/**
	 * mongodb collection & field named linker
	 */
	public static final String MONGODB_NAME_LINKER_SYMBOL = "_";
	
	public static final String MESSAGE_COLLECTION_PREFIX = "message_";
	
	public static final String DATA_MATCH_COLLECTION_NAME_PREFIX = "data_match_";
	
	public static final int MONGODB_BATCH_OPERATION_SIZE = 100;

	/**
	 * statisTime按时间排序totalCount总条数排序successCount成功数排序 failedCount失败数排序 timeoutCount超时数排序 unkownCount未知数排序
	 */
	public static final Map<String,String> ORDER_COLUMN=new HashMap<String,String>(){
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		{
			put("statisTime","statis_time");
			put("totalCount","total_count");
			put("successCount","success_count");
			put("failedCount","failed_count");
			put("timeoutCount","timeout_count");
			put("unkownCount","unkown_count");
		}
	};

	/**
	 * true 降序  false 升序
	 */
	public static final Map<Boolean,Sort.Direction> ORDER_DIRECTION=new HashMap<Boolean, Sort.Direction>(){
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		{
			put(true,Sort.Direction.DESC);
			put(false,Sort.Direction.ASC);
		}
	};
}

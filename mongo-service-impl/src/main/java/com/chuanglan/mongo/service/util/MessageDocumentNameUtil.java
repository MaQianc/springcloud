/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.util;

import java.util.Date;

import org.springframework.util.Assert;

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.constant.Constants;

/**
 * @author      Bomb
 * create-time  2019-01-17 16:58:00
 */
public class MessageDocumentNameUtil {

	/**
	 * dynamic get mongodb collection name
	 * @param date
	 * @param account
	 * @param type
	 * @return
	 */
	public static String getMessageDocumentname(Date date) {
		Assert.notNull(date, "日期不能为null");
		return Constants.MESSAGE_COLLECTION_PREFIX+DateTimeUtil.formatDate(date,"yyyy_MM");
	}
	
	/**
	 * 获取匹配数据的集合名称
	 * @param advertAccount 广告主，自助通账号
	 * @param batchNo ERP提交批次号
	 * @return String collection name
	 */
	public static String getMatchDataPackageCollectionName(String batchNo) {
		return Constants.DATA_MATCH_COLLECTION_NAME_PREFIX +batchNo;
	}
}

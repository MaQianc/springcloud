/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-09 11:47:01
 */
@Data
@Document("message_daily_statis")
public class MessageDailyStatis {

	@Id
	private String id;
	
	/**
	 * flux N account.
	 */
	@Indexed(name="flux_idx")
	@Field("flux_account")
	private String fluxAccount;
	
	/**
	 * flux account name.
	 */
	@Field("flux_name")
	private String fluxName;
	
	/**
	 * adverter account.
	 */
	@Indexed(name="advertiser_idx")
	@Field("advertiser_account")
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@Field("advertiser_name")
	private String advertiserName;
	
	/**
	 * convert flag, whether use 52.cn ,0: false,   1: true . 
	 */
	@Field("convert_flg")
	private Integer convertFlg;
	
	/**
	 * advert
	 */
	@Field("template_id")
	private Integer templateId;
	
	/**
	 * template subject
	 */
	@Field("template_subject")
	private String templateSubject;
	
	
	/**
	 * 未知状态
	 */
	@Field("unkown_count")
	private Integer unkownCount = 0;
	
	@Field("unkown_cost")
	private Long unkownCost = 0l;
	
	/**
	 * 成功状态数量
	 */
	@Field("success_count")
	private Integer successCount = 0;
	
	@Field("success_cost")
	private Long successCost = 0l;
	
	/**
	 * 失败数量
	 */
	@Field("failed_count")
	private Integer failedCount = 0;
	
	@Field("failed_cost")
	private Long failedCost = 0l;
	
	/**
	 * 超时数量
	 */
	@Field("timeout_count")
	private Integer timeoutCount = 0;
	
	@Field("timeout_cost")
	private Long timeoutCost = 0l;
	
	/**
	 * 总数
	 */
	@Field("total_count")
	private Integer totalCount = 0;
	
	/**
	 * create time.
	 */
	@Indexed(name="statis_time_idx")
	@Field("statis_time")
	private Date statisTime;
	
	@Field("template_type")
	private Integer templateType;
	
}

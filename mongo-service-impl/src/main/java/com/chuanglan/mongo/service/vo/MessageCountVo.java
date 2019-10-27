/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-15 17:28:06
 */
@Data
public class MessageCountVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8653005423815361020L;

	/**
	 * flux N account.
	 */
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
	@Field("advertiser_account")
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@Field("advertiser_name")
	private String advertiserName;
	
	/**
	 * message status, 0:unkown 1:success 2:failed.
	 */
	@Field("msg_status")
	private Integer msgStatus;
	
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
	 * counter.
	 */
	private Integer counter;
	
	/**
	 * 統計時間
	 */
	private Date statisTime;
	
	/**
	 * summing cost.
	 */
	private Long cost;
	
	@Field("template_type")
	private Integer templateType;
}

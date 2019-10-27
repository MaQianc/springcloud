/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.vo;

import java.io.Serializable;
import java.util.Date;

import com.chuanglan.advert.common.jackson.MaskMobileJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-17 15:44:54
 */
@ApiModel(description="message view object")
@Data
public class MessageVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1302072194832563164L;

	/**
	 * flux N account.
	 */
	@ApiModelProperty("flux account number")
	private String fluxAccount;
	
	/**
	 * flux account name.
	 */
	@ApiModelProperty("flux account name")
	private String fluxName;
	
	/**
	 * adverter account.
	 */
	@ApiModelProperty("adverter account number")
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@ApiModelProperty("adverter account name")
	private String advertiserName;
	
	/**
	 * msg mobile.
	 */
	@ApiModelProperty("sended mobile number")
	@JsonSerialize(using=MaskMobileJsonSerializer.class)
	private String mobile;
	
	/**
	 * message append content.
	 */
	@ApiModelProperty("sended message content")
	private String content;
	
	/**
	 * short url.
	 */
	@ApiModelProperty("short url address")
	private String shortUrl;
	
	/**
	 * message id.
	 */
	@ApiModelProperty("sended message id")
	private String msgId;
	
	/**
	 * message status, 0:unkown 1:success 2:failed.
	 */
	@ApiModelProperty("message status")
	private Integer msgStatus;
	
	/**
	 * create time.
	 */
	@ApiModelProperty("create time")
	private Date createTime;
	
	/**
	 * update time.
	 */
	@ApiModelProperty("update time")
	private Date updateTime;
	
	/**
	 * convert flag, whether use 52.cn ,0: false,   1: true . 
	 */
	@ApiModelProperty("whether convert url flag")
	private Integer convertFlg;
	
	/**
	 * advert
	 */
	@ApiModelProperty("template id")
	private Integer templateId;
	
	/**
	 * template subject
	 */
	@ApiModelProperty("template_subject")
	private String templateSubject;
}

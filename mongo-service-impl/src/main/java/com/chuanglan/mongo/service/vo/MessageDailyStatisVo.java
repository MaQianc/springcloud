/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-17 20:04:28
 */
@Data
@ApiModel(description="message daily statis view object")
public class MessageDailyStatisVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5859250991827465567L;

	/**
	 * flux N account.
	 */
	@ApiModelProperty("flux account")
	private String fluxAccount;
	
	/**
	 * flux account name.
	 */
	@ApiModelProperty("flux account name")
	private String fluxName;
	
	/**
	 * adverter account.
	 */
	@ApiModelProperty("adverter account")
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@ApiModelProperty("adverter account name")
	private String advertiserName;	
	
	/**
	 * 未知状态
	 */
	@ApiModelProperty("unkown message count")
	private Integer unkownCount = 0;
	
	/**
	 * 成功状态数量
	 */
	@ApiModelProperty("success message count")
	private Integer successCount = 0;
	
	/**
	 * 失败数量
	 */
	@ApiModelProperty("failed message count")
	private Integer failedCount = 0;
	
	/**
	 * 广告超时数量
	 */
	@ApiModelProperty("timeout message count")
	private Integer timeoutCount = 0;
	
	/**
	 * 总数
	 */
	@ApiModelProperty("total sended message count")
	private Integer totalCount = 0;
	
	/**
	 * create time.
	 */
	@ApiModelProperty("statis time")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date statisTime;
	
	
	/**
	 * convert flag, whether use 52.cn ,0: false,   1: true . 
	 */
	@ApiModelProperty("convert flg")
	private Integer convertFlg;
	
	/**
	 * advert
	 */
	@ApiModelProperty("template id")
	private Integer templateId;
	
	/**
	 * template subject
	 */
	@ApiModelProperty("template subject")
	private String templateSubject;
}

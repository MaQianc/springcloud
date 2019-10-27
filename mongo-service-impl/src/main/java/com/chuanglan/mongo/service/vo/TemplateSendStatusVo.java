/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author      Bomb
 * create-time  2019-02-23 11:49:01
 */
@ApiModel(description="template send status")
@Data
public class TemplateSendStatusVo {
	
	/**
	 * 模板id
	 */
	@ApiModelProperty("template id")
	//@Field("_id")
	private Integer templateId;
	
	@ApiModelProperty("advertiser account")
	private String advertiserAccount;
	
	@ApiModelProperty("flux account")
	private String fluxAccount;
	
	/**
	 * 未知总数
	 */
	@ApiModelProperty("unkown count")
	private Integer unkownCount;
	
	/**
	 * 成功总数
	 */
	@ApiModelProperty("success count")
	private Integer successCount;
	
	/**
	 * 失败总数
	 */
	@ApiModelProperty("failed count")
	private Integer failedCount;
	
	/**
	 * 超时总数
	 */
	@ApiModelProperty("timeout count")
	private Integer timeoutCount;
	
	/*
	 * 发送总数
	 */
	@ApiModelProperty("total count")
	private Integer totalCount;

	@ApiModelProperty("statis time")
	private Date statisTime;
}

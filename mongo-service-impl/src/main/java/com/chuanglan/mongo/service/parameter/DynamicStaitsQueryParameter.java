/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author      Bomb
 * create-time  2019-02-28 11:52:57
 */
@Data
@ApiModel(description="page query message parameter")
@ToString
public class DynamicStaitsQueryParameter {

	@ApiModelProperty("template ids")
	private List<Integer> templateIds;

    @ApiModelProperty("advertiser name")
	private String advertiserName;

    @ApiModelProperty("flux name")
	private String fluxName;

    @ApiModelProperty("template subject")
	private String templateSubject;
	
	@ApiModelProperty("advertiser account")
	private String advertiserAccount;
	
	@ApiModelProperty("flux account")
	private String fluxAccount;
	
	@ApiModelProperty("query type,0：startis time 1:templdate ids,2:advertiser account,3:flux account")
	@NotNull(message="查询类型不能为空")
	private Integer queryType;
	
	/**
	 * query start time;
	 */
	@ApiModelProperty("query start time")
	private Date startTime;
	
	@ApiModelProperty("query end time")
	private Date endTime;
}

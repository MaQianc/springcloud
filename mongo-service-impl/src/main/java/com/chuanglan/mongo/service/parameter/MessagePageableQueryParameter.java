/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.chuanglan.advert.common.parameter.AbstractPageableParameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author      Bomb
 * create-time  2019-01-17 15:08:16
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ApiModel(description="page query message parameter")
@ToString(callSuper=true)
public class MessagePageableQueryParameter extends AbstractPageableParameter {

	/**
	 * flux account number
	 */
	@ApiModelProperty("flux account number")
	private String fluxAccount;
	
	/**
	 * flux name
	 */
	@ApiModelProperty("flux name")
	private String fluxName;
	
	/**
	 * adverter number
	 */
	@ApiModelProperty("advertiser account number")
	private String advertiserAccount;
	
	/**
	 * advertiser name
	 */
	@ApiModelProperty("advertiser name")
	private String advertiserName;
	
	/**
	   * 开始时间
	 */
	@ApiModelProperty(value="start time",required=true)
	@NotNull(message="开始时间不能为空")
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	@ApiModelProperty(value="end time",required=true)
	@NotNull(message="结束时间不能为空")
	private Date endTime;
	
	/**
	 * 模板ID
	 */
	@ApiModelProperty("template id")
	private Integer templateId;
	
	/**
	 * 广告主题
	 */
	@ApiModelProperty("template subject")
	private String templateSubject;
	
	@ApiModelProperty("order by coloumn")
	private String orderCol="statisTime";

	@ApiModelProperty("order by direction")
	private Boolean ascFlg=true;
	
}

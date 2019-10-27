/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-09 19:59:26
 */
@ApiModel(description="adding message paramter ")
@Data
public class MessageParamter {

	/**
	 * flux N account.
	 */
	@ApiModelProperty(value="flux account number",required=true)
	@NotEmpty(message="流量主账号不能为空")
	private String fluxAccount;
	
	/**
	 * flux account name.
	 */
	@NotEmpty(message="流量主名称不能为空")
	@ApiModelProperty(value="flux company name",required=true)
	private String fluxName;
	
	/**
	 * adverter account.
	 */
	@NotEmpty(message="广告主账号不能为空")
	@ApiModelProperty(value="customer account number",required=true)
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@NotEmpty(message="广告主名称不能为空")
	@ApiModelProperty(value="customer company number",required=true)
	private String advertiserName;
	
	/**
	 * msg mobile.
	 */
	@NotEmpty(message="手机号不能为空")
	@ApiModelProperty(value="messaged mobile ",required=true)
	private String mobile;
	
	/**
	 * message append content.
	 */
	@NotEmpty(message="广告内容不能为空")
	@ApiModelProperty(value="message appended content",required=true)
	private String content;
	
	/**
	 * short url.
	 */
	@ApiModelProperty(value="advert short url",required=true)
	private String shortUrl;
	
	/**
	 * message id.
	 */
	@NotEmpty(message="短信id不能为空")
	@ApiModelProperty(value="short message id",required=true)
	private String msgId;
	
	
	/**
	 * convert flag, whether use 52.cn ,0: false,   1: true . 
	 */
	@NotNull(message="转换标志不能为空")
	@ApiModelProperty(value="convert to short url flag",required=true)
	private Integer convertFlg;
	
	/**
	 * advert
	 */
	@NotNull(message="广告模板id不能为空")
	@ApiModelProperty(value="template id",required=true)
	private Integer templateId;
	
	@NotEmpty(message="广告主题")
	@ApiModelProperty(value="template subject",required=true)
	private String templateSubject;
	
	@ApiModelProperty(value="msg status",required=false)
	private Integer msgStatus = 0;


	@ApiModelProperty(value="cost",required=false)
	private Long cost = 0l;
	
	@ApiModelProperty(value="template type",required=false)
	private Integer templateType = 1;


}

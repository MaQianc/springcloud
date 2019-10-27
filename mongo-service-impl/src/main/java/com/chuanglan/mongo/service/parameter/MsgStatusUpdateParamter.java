/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-14 19:11:29
 */
@Data
@ApiModel(description="message status update parameter")
public class MsgStatusUpdateParamter {

	/**
	 * message id
	 */
	@NotEmpty(message="消息id不能为空")
	private String msgId;
	
	
	/**
	 * message sended status
	 */
	@NotNull(message="消息发送状态不能为空")
	@Range(min=1,max=3,message="状态值不能小于{0}且大于{1}")
	private Integer status;
}

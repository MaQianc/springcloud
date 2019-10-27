/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.chuanglan.advert.common.request.Request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author      Bomb
 * create-time  2019-01-09 19:43:51
 */
@ApiModel(description="batch add message paramter")
@Data
@EqualsAndHashCode(callSuper=true)
public class BatchAddMsgParameter extends Request{
	
	@NotEmpty(message="消息不能为空")
	@Valid
	private List<MessageParamter> messages;
	
}

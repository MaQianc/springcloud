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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author      Bomb
 * create-time  2019-01-14 19:14:55
 */
@ApiModel(description="batch update message status parameter")
@Data
@EqualsAndHashCode(callSuper=true)
public class BatchUpdateMsgParameter extends Request {

	@Valid
	@NotEmpty(message="修改消息不能为空")
	@ApiModelProperty(value=" udpate message status parameter ")
	private List<MsgStatusUpdateParamter> datas;
}

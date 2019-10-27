/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.parameter;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.chuanglan.advert.common.request.Request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author      Bomb
 * create-time  2019-01-17 19:24:20
 */
@ApiModel(description="daily statis parameter")
@Data
@EqualsAndHashCode(callSuper=true)
public class DailyStatisParameter extends Request {

	@ApiModelProperty(value="statis date",required=true)
	@NotNull(message="统计日期不能为空")
	private Date date;
}

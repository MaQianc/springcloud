/**
 * 
 */
package com.chuanglan.mongo.service.parameter;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ChuangLan
 *
 */
@Data
@ApiModel
public class PhoneMatchParameter {
	
	/**
	 * 批次号
	 */
	@ApiModelProperty(value="提交批次号",required=true)
	@NotEmpty(message="批次号不能为空")
	private String batchNo;
	
	/**
	 * 加密后的手机内容
	 */
	@ApiModelProperty(value="匹配数据",required=true)
	@NotEmpty(message="匹配数据不能为空")
	private String encryptMobile;
	
}

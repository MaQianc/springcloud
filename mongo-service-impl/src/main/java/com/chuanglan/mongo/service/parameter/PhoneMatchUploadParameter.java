/**
 * 
 */
package com.chuanglan.mongo.service.parameter;

import java.util.List;

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
public class PhoneMatchUploadParameter {
	
	/**
	 * 批次号
	 */
	@ApiModelProperty(value="提交批次号",required=true)
	@NotEmpty(message="批次号不能为空")
	private String batchNo;
	
	/**
	 * 上传的加密后的手机号
	 */
	@ApiModelProperty(value="加密手机号",required=true)
	@NotEmpty(message="加密手机号不能为空")
	private List<String> mobiles;
}

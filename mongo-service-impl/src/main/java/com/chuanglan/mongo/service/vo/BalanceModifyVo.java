/**
 * 
 */
package com.chuanglan.mongo.service.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;


/**
 * @author ChuangLan
 *
 */
@Data
public class BalanceModifyVo {

	/**
	 * 事务编号
	 */
	private String transId;
	
	/**
	 * 账户号，手机号
	 */
	private String accountNo;
	
	/**
	 * 调整金额
	 */
	private BigDecimal amount;
	
	/**
	 * 修改渠道
	 */
	private Integer channel;
	
	/**
	 * 提交时间
	 */
	private Date commitDate;

	
	
}

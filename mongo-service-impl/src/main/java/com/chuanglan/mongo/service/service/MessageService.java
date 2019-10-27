/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.parameter.MessageParamter;
import com.chuanglan.mongo.service.parameter.MsgStatusUpdateParamter;
import com.chuanglan.mongo.service.vo.MessageVo;

/**
 * @author      Bomb
 * create-time  2019-01-09 20:26:15
 */
public interface MessageService {

	/**
	 * batch save sended message.
	 * @param messages List
	 */
	int save(List<MessageParamter> messages)throws Exception;
	
	/**
	 * batch update sended message.
	 * @param statusParameters
	 */
	int updateStatus(List<MsgStatusUpdateParamter> statusParameters);
	
	/**
	 * page query message details
	 * @param parameter MessagePageableQueryParameter
	 * @return Page<MessageVo>
	 */
	Page<MessageVo> pageQuery(MessagePageableQueryParameter parameter);
	
}

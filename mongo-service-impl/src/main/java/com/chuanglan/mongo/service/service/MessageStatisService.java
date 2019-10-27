/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.service;

import java.util.Date;
import java.util.List;

import com.chuanglan.mongo.service.resp.TemplateSendCountResp;
import org.springframework.data.domain.Page;

import com.chuanglan.mongo.service.parameter.DynamicStaitsQueryParameter;
import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;

/**
 * @author      Bomb
 * create-time  2019-01-16 09:39:32
 */
public interface MessageStatisService {

	/**
	 * used for daily task
	 */
	void dailyStatis(Date date);
	
	/*
	 * pageabled query message statis details
	 */
	Page<MessageDailyStatisVo> pageQuery(MessagePageableQueryParameter parameter);
	
	/**
	 * realtime statis, one time per 15 minutes
	 */
	void realTimeStatis();
	
	/**
	 * query template send status
	 * @param templateIds List<Integer>
	 * @return
	 */
	List<TemplateSendStatusVo> queryTemplateSendStatus(DynamicStaitsQueryParameter parameter);


    /**
     * group by fluxaccount query send statis
     * @param parameter
     * @return
     */
	List<String> queryStatisFluxAccount(MessagePageableQueryParameter parameter );


	/**
	 *  group by templateId query successCount
	 * @param ids
	 * @return
	 */
	List<TemplateSendCountResp> queryTemplateSuccessCount(List<Integer> ids);


}

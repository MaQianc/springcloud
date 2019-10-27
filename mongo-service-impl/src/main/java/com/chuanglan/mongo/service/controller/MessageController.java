/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuanglan.advert.common.constant.CommonConstant;
import com.chuanglan.advert.common.response.Response;
import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.parameter.BatchAddMsgParameter;
import com.chuanglan.mongo.service.parameter.BatchUpdateMsgParameter;
import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.parameter.MsgStatusUpdateParamter;
import com.chuanglan.mongo.service.service.MessageService;
import com.chuanglan.mongo.service.vo.MessageVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      Bomb
 * create-time  2019-01-09 19:32:38
 */
@Api(value="message operations")
@RestController
@RequestMapping("/message")
@Validated
@Slf4j
public class MessageController {

	@Autowired
	private MessageService messageService;
	
	/**
	 * batch insert sended adverter message.
	 * @param saveParameter BatchAddMsgParameter
	 * @return Response
	 */
	@ApiOperation(value = " batch insert sended adverter message")
	@PostMapping("batch-save")
	public Response<Integer> batchSaveMessage(@RequestBody @Valid BatchAddMsgParameter saveParameter) throws Exception{
		long start = System.currentTimeMillis();
		int savedCount = messageService.save(saveParameter.getMessages());
		if(log.isDebugEnabled()) {
			log.debug(String.format("batch save msg cost time is : %d", System.currentTimeMillis()-start));
		}
		return Response.success(savedCount);
	}
	
	/**
	 * batch update sended message status.
	 * @param updateParameter
	 * @return Response
	 */
	@ApiOperation(value="batch updaste sended message status")
	@PostMapping("batch-update-status")
	public Response<Integer> batchUpdateMsgStatus(@RequestBody @Valid BatchUpdateMsgParameter updateParameter){
		long start = System.currentTimeMillis();
		List<MsgStatusUpdateParamter> parameters = updateParameter.getDatas();
		int updatedCount = messageService.updateStatus(parameters);
		if(log.isDebugEnabled()) {
			log.debug(String.format("batch update msg cost time is : %d", System.currentTimeMillis()-start));
		}
		return Response.success(updatedCount);
	}

	/**
	 * page query message by account in a duration time.
	 * @param parameter MessagePageableParameter
	 * @return Response
	 */
	@ApiOperation("page query message")
	@GetMapping("page-query")
	public Response<Page<MessageVo>> pageQueryMessage(@Valid MessagePageableQueryParameter parameter){
		if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
			throw new IllegalArgumentException("查询开始时间不能大于结束时间");
		}
		String startYearMonthStr = DateTimeUtil.formatDate(parameter.getStartTime(), CommonConstant.YEAR_MONTH_FORMAT_PATTERN);
		String endYearMonthStr = DateTimeUtil.formatDate(parameter.getEndTime(), CommonConstant.YEAR_MONTH_FORMAT_PATTERN);
		if(!startYearMonthStr.equals(endYearMonthStr)) {
			throw new IllegalArgumentException("查询起始时间必须是在同一个月内");
		}
		Page<MessageVo> page = messageService.pageQuery(parameter);
		return Response.success(page);
	}

	
	
}

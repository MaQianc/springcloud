/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.chuanglan.mongo.service.parameter.TemplateSuccessCountReq;
import com.chuanglan.mongo.service.resp.TemplateSendCountResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.chuanglan.advert.common.request.Request;
import com.chuanglan.advert.common.response.Response;
import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.parameter.DailyStatisParameter;
import com.chuanglan.mongo.service.parameter.DynamicStaitsQueryParameter;
import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.service.MessageStatisService;
import com.chuanglan.mongo.service.type.DynamicStaitsQueryType;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      Bomb
 * create-time  2019-01-17 19:06:25
 */
@Api(value="message statis operations")
@RestController
@RequestMapping("/message-statis")
@Validated
@Slf4j
public class MessageStatisController {
	
	@Autowired
	private MessageStatisService statisService;

	@ApiOperation("daily calculate sended messages")
	@PostMapping("calculate")
	public Response<?> dailyStatis(@RequestBody @Valid DailyStatisParameter parameter){
		log.info(String.format("begin calculate the date : %tF", parameter.getDate()));
		statisService.dailyStatis(parameter.getDate());
		return Response.success();
	}
	
	@ApiOperation("realtime calculate sended messages")
	@PostMapping("realtime-calculate")
	public Response<?> realtimeStatis(@RequestBody @Valid Request parameter){
		log.info(String.format("begin realtime calculate the time : %s", DateTimeUtil.getCurDateTime()));
		statisService.realTimeStatis();
		return Response.success();
	}
	
	@ApiOperation("daily abnormal message offset")
	@PostMapping("offset")
	public Response<?> dailyAbnormalOffset(@RequestBody Date date){
		//TODO
		return Response.success();
	}
	
	@ApiOperation("pageable query statis message data")
	@GetMapping("page-query")
	public Response<Page<MessageDailyStatisVo>> pageableQuery(MessagePageableQueryParameter parameter ){
		if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
			if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
				throw new IllegalArgumentException("查询开始时间不能大于结束时间");
			}
		}
		Page<MessageDailyStatisVo> pageDatas = this.statisService.pageQuery(parameter);
		return Response.success(pageDatas);
	}
	
	/**
	 * query templdate send statis
	 * @param templdateIds List
	 * @return Response<List<TemplateSendStatusVo>>
	 */
	@ApiOperation("query templdate sended statis data by ids")
	@GetMapping("query-send-statis")
	public Response<List<TemplateSendStatusVo>> querySendStatis(DynamicStaitsQueryParameter parameter){
		//参数验证
		if(DynamicStaitsQueryType.TIME.getType().equals(parameter.getQueryType())){
			if(parameter.getStartTime()==null || parameter.getEndTime()==null) {
				throw new IllegalArgumentException("查询起始结束时间不能为空");
			}
			if(parameter.getStartTime().compareTo(parameter.getEndTime())>=0){
				throw new IllegalArgumentException("查询开始时间不能大于结束时间");
			}
		}else if(DynamicStaitsQueryType.TEMPLATE.getType().equals(parameter.getQueryType())) {
			if(CollectionUtils.isEmpty(parameter.getTemplateIds())) {
				throw new IllegalArgumentException("广告模板不能为空");
			}
		}else if(DynamicStaitsQueryType.ADVERTISER.getType().equals(parameter.getQueryType())) {
			if(StringUtils.isBlank(parameter.getAdvertiserAccount())) {
				throw new IllegalArgumentException("广告主账号不能为空");
			}
		}else if(DynamicStaitsQueryType.FLUX.getType().equals(parameter.getQueryType())) {
			if(StringUtils.isBlank(parameter.getFluxAccount())) {
				throw new IllegalArgumentException("流量主账号不能为空");
			}
		}else {
			throw new IllegalArgumentException("查询类型错误,只能是1-2-3");
		}
		if((null == parameter.getStartTime() && null != parameter.getEndTime()) ||
				(null != parameter.getStartTime() && null == parameter.getEndTime())) {
			throw new IllegalArgumentException("查询起止时间必须都为空或都不为空");
		}
		if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
			if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
				throw new IllegalArgumentException("查询开始时间不能大于结束时间");
			}
		}
		List<TemplateSendStatusVo> list = this.statisService.queryTemplateSendStatus(parameter);
		return Response.success(list);
	}


    @ApiOperation("group by fluxaccount query send statis")
    @GetMapping("query-fluxaccount")
	public Response<List<String>> queryStatisFluxAccount(MessagePageableQueryParameter parameter ){
        if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
            if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
                throw new IllegalArgumentException("查询开始时间不能大于结束时间");
            }
        }
        List<String> strings = statisService.queryStatisFluxAccount(parameter);
        return Response.success(strings);
    }

	@ApiOperation("group by templateId query successCount")
	@PostMapping("template-success-count")
	public Response<List<TemplateSendCountResp>> queryStatisFluxAccount(@RequestBody TemplateSuccessCountReq req){
		List<TemplateSendCountResp> templateSendCountResps = statisService.queryTemplateSuccessCount(req.getIds());
		return Response.success(templateSendCountResps);
	}
	
}

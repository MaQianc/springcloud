/**
 * 
 */
package com.chuanglan.mongo.service.controller;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuanglan.advert.common.response.Response;
import com.chuanglan.mongo.service.service.CostOffsetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ChuangLan
 *
 */
@Api(value="cost offset operations")
@RestController
@RequestMapping("/cost-offset")
@Validated
@Slf4j
public class CostOffsetController {

	@Autowired
	private CostOffsetService service;
	
//	@ApiOperation("failed offset")
//	@PostMapping("/failed")
//	public Response<Boolean> failedOffset(@Valid @RequestBody @NotNull(message="补偿日期不能为空") Date date) {
//		log.info("failed offset begin starting");
//		return Response.success(service.failedOffSet(date));
//
//	}
	
	@ApiOperation("unkown offset")
	@PostMapping("/unkown")
	public Response<Boolean> unknowOffset(@Valid @RequestBody @NotNull(message="补偿日期不能为空") Date date) {
		log.info("unkown offset begin starting");
		return Response.success(service.unkownOffSet(date));
		 
	}
}

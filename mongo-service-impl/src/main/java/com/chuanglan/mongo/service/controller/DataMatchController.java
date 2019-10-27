/**
 * 
 */
package com.chuanglan.mongo.service.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuanglan.advert.common.exception.ErrorConstants;
import com.chuanglan.advert.common.response.Response;
import com.chuanglan.mongo.service.parameter.PhoneMatchParameter;
import com.chuanglan.mongo.service.parameter.PhoneMatchUploadParameter;
import com.chuanglan.mongo.service.service.DataMatchService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ChuangLan
 * 装库数据匹配
 */
@Api(value="advertister encrypted mobile match")
@RestController
@Validated
@RequestMapping("/data-match")
@Slf4j
public class DataMatchController {

	@Autowired
	private DataMatchService service;
	
	@ApiOperation("upload encrypt data")
	@PostMapping("/upload")
	public Response<Integer> uploadMatchData(@RequestBody @Valid PhoneMatchUploadParameter parameter){
		long start = System.currentTimeMillis();
		Optional<Integer> op = service.uploadMatchData(parameter);
		if(op.isPresent()) {
			log.info("update {} data cost time is : {}",op.get(),(System.currentTimeMillis()-start));
			return Response.success(op.get());
		}else {
			return Response.fail(ErrorConstants.System_Error, 0);
		}
	}
	
	@ApiOperation("hit encrypt data")
	@GetMapping("/hit")
	public Response<Boolean> matchData(@Valid PhoneMatchParameter parameter){
		long start = System.currentTimeMillis();
		Optional<Boolean> op = service.isMobileMatch(parameter);
		if(op.isPresent()) {
			log.info("find data : {} & result is : {} & cost time is : {}",parameter.toString(),op.get(),(System.currentTimeMillis()-start));
			return Response.success(op.get());
		}else {
			return Response.fail(ErrorConstants.System_Error, Boolean.FALSE);
		}
	}
	
}

package com.chuanglan.mongo.service.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuanglan.advert.common.response.Response;
import com.chuanglan.mongo.service.parameter.FluxAccountQueryMessageParameter;
import com.chuanglan.mongo.service.service.FluxAccountMessageService;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="flux account message statis")
@RestController
@RequestMapping("/fluxAccount/")
@Validated
public class FluxAccountMessageController {

    @Autowired
    FluxAccountMessageService fluxAccountMessageService;

    @ApiOperation("flux account pageable query statis message data")
    @GetMapping("page-query")
    public Response<Page<MessageDailyStatisVo>> pageableQuery(@Valid FluxAccountQueryMessageParameter parameter ){
        if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
            if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
                throw new IllegalArgumentException("查询开始时间不能大于结束时间");
            }
        }
        Page<MessageDailyStatisVo> pageDatas = this.fluxAccountMessageService.queryPage(parameter);
        return Response.success(pageDatas);
    }


    @ApiOperation("flux account pageable query statis message data")
    @GetMapping("query")
    public Response<List<MessageDailyStatisVo>> query(@Valid FluxAccountQueryMessageParameter parameter ){
        if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
            if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
                throw new IllegalArgumentException("查询开始时间不能大于结束时间");
            }
        }
        List<MessageDailyStatisVo> pageDatas = this.fluxAccountMessageService.query(parameter);
        return Response.success(pageDatas);
    }

    @ApiOperation("flux account pageable query statis message data")
    @GetMapping("querySend")
    public Response<List<TemplateSendStatusVo>> queryFluxTemplateSendStatus(@Valid FluxAccountQueryMessageParameter parameter ){
        if(null != parameter.getStartTime() && null!= parameter.getEndTime()) {
            if(parameter.getStartTime().compareTo(parameter.getEndTime()) >= 0) {
                throw new IllegalArgumentException("查询开始时间不能大于结束时间");
            }
        }
        List<TemplateSendStatusVo> templateSendStatusVos = this.fluxAccountMessageService.queryFluxTemplateSendStatus(parameter);
        return Response.success(templateSendStatusVos);
    }
}

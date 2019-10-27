package com.chuanglan.data.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.chuanglan.advert.common.request.Request;
import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.advert.common.util.JacksonUtil;
import com.chuanglan.mongo.service.Application;
import com.chuanglan.mongo.service.documents.MessageDailyStatis;
import com.chuanglan.mongo.service.parameter.DailyStatisParameter;
import com.mongodb.client.result.DeleteResult;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@FixMethodOrder(MethodSorters.DEFAULT)
public class MessageStatisControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate template;
	
	@Test
	public void testDailyStatis() throws Exception {
		DailyStatisParameter parameter = new DailyStatisParameter();
		parameter.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
		parameter.setDate(DateTimeUtil.getCurrentDateTime());
		String requestBody = JacksonUtil.toJson(parameter);
		//第一次新增
		mockMvc.perform(post("/message-statis/calculate").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestBody).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(0));
		Thread.sleep(10000);
		//第二次重复分析，update数据
		parameter.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
		requestBody = JacksonUtil.toJson(parameter);
		mockMvc.perform(post("/message-statis/calculate").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestBody).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(0));
	}
	
	@Test
	public void testRealtimeStatis() throws Exception{
		Request request = new Request();
		request.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
		String requestBody = JacksonUtil.toJson(request);
		mockMvc.perform(post("/message-statis/realtime-calculate").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestBody).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(0));
	}

	@Test
	public void testPageableQuery() throws Exception{
		Thread.sleep(10000);
		String beginTime = DateTimeUtil.formatDate(DateTimeUtil.getBeginTimeOfDate(DateTimeUtil.getCurrentDateTime()));
		String endTime = DateTimeUtil.formatDate(DateTimeUtil.getEndTimeOfDate(DateTimeUtil.getCurrentDateTime()));
		MultiValueMap<String, String> paraMap = new LinkedMultiValueMap<>();
		paraMap.add("startTime", endTime);
		paraMap.add("endTime", beginTime);
		mockMvc.perform(get("/message-statis/page-query").contentType(MediaType.TEXT_HTML).params(paraMap)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.code").value(100003));
		//正常测试--流量主
		paraMap.remove("startTime");
		paraMap.remove("endTime");
		paraMap.add("startTime", beginTime);
		paraMap.add("endTime", endTime);
		paraMap.add("fluxAccount", "flux-account-1");
		paraMap.add("fluxName", "flux-account-1");
		paraMap.add("advertiserAccount", "advertiser-account-1");
		paraMap.add("advertiserName", "advertiser-account-1");
		paraMap.add("templateId", "999999");
		paraMap.add("templateSubject", "testcase回归测试");
		mockMvc.perform(get("/message-statis/page-query").contentType(MediaType.TEXT_HTML).params(paraMap)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.code").value(0))
		.andExpect(jsonPath("$.body.numberOfElements").value(1));
		
		DeleteResult result = template.remove(Query.query(Criteria.where("template_id").is(999999)), MessageDailyStatis.class);
		TestCase.assertEquals(1, (int)result.getDeletedCount());
	}

}

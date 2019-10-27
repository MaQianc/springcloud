package com.chuanglan.data.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.advert.common.util.JacksonUtil;
import com.chuanglan.mongo.service.Application;
import com.chuanglan.mongo.service.parameter.BatchAddMsgParameter;
import com.chuanglan.mongo.service.parameter.BatchUpdateMsgParameter;
import com.chuanglan.mongo.service.parameter.MessageParamter;
import com.chuanglan.mongo.service.parameter.MsgStatusUpdateParamter;
import com.chuanglan.mongo.service.util.MessageDocumentNameUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class MessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MongoTemplate template;
	
	private static MongoTemplate sTempalte;
	
	@PostConstruct
	public void init() throws Exception{
		sTempalte = this.template;
	}

	/**
	 * 因为这个操作中有简历集合的操作不能被包含在事务中。只能手动清除
	 * 
	 * @throws Exception
	 */
	@Test
	// @Transactional
	// @Rollback
	public void testBatchSaveMessage() throws Exception {
		BatchAddMsgParameter parameter = new BatchAddMsgParameter();
		parameter.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
		List<MessageParamter> datas = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			MessageParamter message = new MessageParamter();
			message.setFluxAccount("flux-account-1");
			message.setFluxName("flux-account-1");
			message.setAdvertiserAccount("advertiser-account-1");
			message.setAdvertiserName("advertiser-account-1");
			message.setContent("test-case-"+i);
			message.setConvertFlg(1);
			message.setMobile("9990000000" + i);
			message.setMsgId("20200101000000000" + i);
			message.setShortUrl("52.cn/99999" + i);
			message.setTemplateId(999999);
			message.setTemplateSubject("testcase回归测试");
			datas.add(message);
		}
		parameter.setMessages(datas);
		String requestBody = JacksonUtil.toJson(parameter);
		mockMvc.perform(post("/message/batch-save").contentType(MediaType.APPLICATION_JSON_UTF8).content(requestBody)
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(0));

	}

	@Test
	public void testBatchUpdateMsgStatus() throws Exception {
		BatchUpdateMsgParameter parameter = new BatchUpdateMsgParameter();
		parameter.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
		List<MsgStatusUpdateParamter> updateParameters = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			MsgStatusUpdateParamter updateParameter = new MsgStatusUpdateParamter();
			updateParameter.setMsgId("20200101000000000" + i);
			updateParameter.setStatus(ThreadLocalRandom.current().nextInt(1, 3));
			updateParameters.add(updateParameter);
		}
		parameter.setDatas(updateParameters);
		String requestBody = JacksonUtil.toJson(parameter);
		mockMvc.perform(post("/message/batch-update-status").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestBody).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(0));
	}

	@Test
	public void testPageQueryMessage() throws Exception {
		//测试广告主流量主都没有值
		String beginTime = DateTimeUtil.formatDate(DateTimeUtil.getBeginTimeOfDate(DateTimeUtil.getCurrentDateTime()));
		String endTime = DateTimeUtil.formatDate(DateTimeUtil.getEndTimeOfDate(DateTimeUtil.getCurrentDateTime()));
		MultiValueMap<String, String> paraMap = new LinkedMultiValueMap<>();
		paraMap.add("startTime", endTime);
		paraMap.add("endTime", beginTime);
		mockMvc.perform(get("/message/page-query").contentType(MediaType.TEXT_HTML).params(paraMap)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.code").value(100003));
		//开始时间和结束时间不在同一个月内
		paraMap.remove("endTime");
		ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now().plusMonths(1), ZoneId.systemDefault());
		String errorEndtime = DateTimeUtil.formatDate(new Date(zdt.toInstant().toEpochMilli()));
		paraMap.add("endTime", errorEndtime);
		mockMvc.perform(get("/message/page-query").contentType(MediaType.TEXT_HTML).params(paraMap)
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
		mockMvc.perform(get("/message/page-query").contentType(MediaType.TEXT_HTML).params(paraMap)
				.accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.code").value(0))
		.andExpect(jsonPath("$.body.numberOfElements").value(10));
		
	}


	public static void after() throws Exception{
		// 手动清除数据
		String collectionName = MessageDocumentNameUtil.getMessageDocumentname(new Date());
		sTempalte.remove(Query.query(Criteria.where("template_id").is(999999)), collectionName);
		//sTempalte.remove(Query.query(Criteria.where("template_id").is(999999)), MessageDailyStatis.class);
	}
	

}

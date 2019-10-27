/**
 * 
 */
package com.chuanglan.mongo.service.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.chuanglan.mongo.service.documents.ReturnBalanceMessage;
import com.chuanglan.mongo.service.service.ReturnBalanceMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.advert.common.util.JacksonUtil;
import com.chuanglan.mongo.service.documents.MessageDailyStatis;
import com.chuanglan.mongo.service.service.CostOffsetService;
import com.chuanglan.mongo.service.util.SerialNoGenerator;
import com.chuanglan.mongo.service.vo.BalanceModifyVo;
import com.chuanglan.mongo.service.vo.HttpResponse;
import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ChuangLan
 *
 */
@Service
@Slf4j
public class CostOffsetServiceImpl implements CostOffsetService{

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ReturnBalanceMessageService returnBalanceMessageService;

	/**
	 * base url.
	 */
	@Value("${rest.balance.service.baseurl}")
	private String balanceHttpBaseUrl;

	/**
	 * balance batch adjust url.
	 */
	private final String balanceBatchAdjustUrl = "balance/batch-adjust";
	
	
	@Override
	public Boolean failedOffSet(Date date) {
		List<MessageDailyStatis> list = findStatisData(date);
		if (!CollectionUtils.isEmpty(list)) {
			Map<String, List<MessageDailyStatis>> mapData = list.parallelStream()
					.collect(Collectors.groupingBy(MessageDailyStatis::getAdvertiserAccount));
			List<BalanceModifyVo> vos = new ArrayList<>();
			for (String advertisterNo : mapData.keySet()) {
				AtomicLong totalCost = new AtomicLong();
				mapData.get(advertisterNo).forEach(statisData->{
					totalCost.addAndGet(statisData.getFailedCost());
					totalCost.addAndGet(statisData.getTimeoutCost());
				});
				//补偿金额为0的不需要计算
				if(totalCost.get() <= 0) {
					continue;
				}
				BalanceModifyVo vo = new BalanceModifyVo();
				BigDecimal cost = new BigDecimal(totalCost.get()).divide(new BigDecimal(10000));
				vo.setAmount(cost);
				vo.setAccountNo(advertisterNo);
				vo.setChannel(3);//失败补偿
				vo.setCommitDate(DateTimeUtil.getCurrentDateTime());
				vo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
				vos.add(vo);
			}
			if(CollectionUtils.isEmpty(vos)) {
				return true;
			}else {
				return sendBalanceAdjustequest(vos);
			}
		}
		return true;
	}
	
	
	@Override
	public Boolean unkownOffSet(Date date) {
		try {
			Date beginTime = DateTimeUtil.getBeginTimeOfDate(date);
			Date endTime = DateTimeUtil.getEndTimeOfDate(date);
			Query query = Query.query(Criteria.where("statisTime").gte(beginTime).lte(endTime).and("template_type").is(1));
			List<MessageDailyStatis> messageDailyStatis = mongoTemplate.find(query, MessageDailyStatis.class);
			List<BalanceModifyVo> modifyVos=new ArrayList<>();
			List<ReturnBalanceMessage> returnBalanceMessages=new ArrayList<>();
			if(!CollectionUtils.isEmpty(messageDailyStatis)){
				messageDailyStatis.forEach(dailyStatis ->{
					BalanceModifyVo balanceModifyVo=new BalanceModifyVo();
					if(dailyStatis.getUnkownCost()>0){
						balanceModifyVo.setAccountNo(dailyStatis.getAdvertiserAccount());
						balanceModifyVo.setChannel(3);
						BigDecimal cost = new BigDecimal(dailyStatis.getUnkownCost()).divide(new BigDecimal(10000));
						balanceModifyVo.setAmount(cost);
						balanceModifyVo.setCommitDate(DateTimeUtil.getCurrentDateTime());
						balanceModifyVo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
						ReturnBalanceMessage message=new ReturnBalanceMessage();
						message.setAdvertiserAccount(dailyStatis.getAdvertiserAccount());
						message.setReturnTime(DateTimeUtil.getCurrentDateTime());
						message.setUnknowCost(dailyStatis.getUnkownCost());
						returnBalanceMessages.add(message);
						modifyVos.add(balanceModifyVo);
					}
				});
			}
			if(!CollectionUtils.isEmpty(modifyVos)){
				returnBalanceMessageService.save(returnBalanceMessages);
				sendBalanceAdjustequest(modifyVos);
			}
			return true;
		}catch (Exception e){
			log.error("return unknowCost error",e);
		}
		return false;
	}
	
	/**
	 * 获取制定日期的统计记录
	 * 
	 * @param date
	 * @return
	 */
	private List<MessageDailyStatis> findStatisData(Date date) {
		Date beginTime = DateTimeUtil.getBeginTimeOfDate(date);
		Date endTime = DateTimeUtil.getEndTimeOfDate(date);
		Query query = new Query();
		query.addCriteria(new Criteria().and("statisTime").gte(beginTime).lte(endTime));
		List<MessageDailyStatis> statisList = this.mongoTemplate.find(query, MessageDailyStatis.class);
		return statisList;
	}
	
	/**
	 * send balance adjust http request
	 * @param modifyVos List<BalanceModifyVo>
	 * @return Boolean
	 */
	public Boolean sendBalanceAdjustequest(List<BalanceModifyVo> modifyVos) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = JacksonUtil.toJson(modifyVos);
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> response = null;
        try {
        	response = restTemplate.postForEntity(balanceHttpBaseUrl+balanceBatchAdjustUrl, httpEntity, String.class);
        	HttpResponse<String> httpResponse = JacksonUtil.readValue(response.getBody(), new TypeToken<HttpResponse<String>>() {
				private static final long serialVersionUID = 1L;}.getType());
        	if(httpResponse.getCode() == 0 ) {
				return true;
			}else {
				log.info(response.getBody());
			}
        }catch(Exception e) {
        	log.error("invoke sending balance adjust data list faild",e);
        }
        return false;
	}

}

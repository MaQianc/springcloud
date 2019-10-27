/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.chuanglan.mongo.service.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.chuanglan.mongo.service.documents.ReturnBalanceMessage;
import com.chuanglan.mongo.service.service.ReturnBalanceMessageService;
import com.chuanglan.mongo.service.util.SerialNoGenerator;
import com.chuanglan.mongo.service.vo.BalanceModifyVo;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.chuanglan.advert.common.redis.RedisKeyUtils;
import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.constant.Constants;
import com.chuanglan.mongo.service.documents.MessageDailyStatis;
import com.chuanglan.mongo.service.parameter.DynamicStaitsQueryParameter;
import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.resp.TemplateSendCountResp;
import com.chuanglan.mongo.service.service.MessageStatisService;
import com.chuanglan.mongo.service.type.DynamicStaitsQueryType;
import com.chuanglan.mongo.service.type.MessageStatusType;
import com.chuanglan.mongo.service.util.MessageDocumentNameUtil;
import com.chuanglan.mongo.service.util.RedisLock;
import com.chuanglan.mongo.service.vo.MessageCountVo;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Bomb create-time 2019-01-16 11:28:23
 */
@Slf4j
@Service
public class MessageStatisServiceImpl implements MessageStatisService {

	// @Autowired
	// private AdvertFluxSetService advertFluxSetService;

	@Autowired
	private MongoTemplate template;

	@Autowired
	private ValueOperations<String, Object> valueOperations;

	@Autowired
	private RedisKeyUtils keyUtils;

	@Autowired
    private ReturnBalanceMessageService returnBalanceMessageService;

	@Autowired
    private CostOffsetServiceImpl costOffsetServiceImpl;

	private int dailyWaitTimeoutMillisecond = 60 * 5 * 1000;

	private int realTimeWaitTimeoutMillisecond = 2 * 1000;

	private int exprireMillisecond = 2*1000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chuanglan.mongo.service.service.MessageStatisService#dailyStatis()
	 */
	@Override
	@Async
	// @CacheEvict(cacheNames="message-static-query",key="MessageStatisServiceImpl:pageQuery:*",allEntries=true)
	public void dailyStatis(Date date) {
		// 统计不能并行执行，每次只能跑一个
		RedisLock lock = new RedisLock(valueOperations, keyUtils.getKey("advert-sms-dailystatis"), dailyWaitTimeoutMillisecond,
				exprireMillisecond);
		try {
			if (!lock.lock()) {
				log.info("can't fetch data statis locker!");
				return;
			}
    		long startTime = System.currentTimeMillis();
			List<MessageDailyStatis> dailyStatisList = new ArrayList<>();
			Map<Integer, Map<Date, List<MessageCountVo>>> mappedData = statisMessage(date, 3);
			mappedData.forEach((templateId, dateMappedData) -> {
				dateMappedData.forEach((statisDate, voList) -> {
					MessageDailyStatis dailyStatis = null;
					for (MessageCountVo countVo : voList) {
						if (null == dailyStatis) {
							dailyStatis = new MessageDailyStatis();
						}
						if(countVo.getTemplateId().equals(dailyStatis.getTemplateId())){
							dailyStatis.setTemplateSubject(countVo.getTemplateSubject());
							Integer counter = countVo.getCounter();
							Long cost = countVo.getCost();
							if (MessageStatusType.FAILED.getValue().equals(countVo.getMsgStatus())) {
								Integer failedCount = dailyStatis.getFailedCount();
								dailyStatis.setFailedCount(failedCount);
								dailyStatis.setFailedCost(cost);
							} else dailyStatisUpdate(dailyStatis, countVo, counter, cost);
						}else{
							BeanUtils.copyProperties(countVo, dailyStatis, "status", "counter");
							dailyStatisSave(dailyStatis, countVo);
						}
					}
					if (null != dailyStatis) {
						dailyStatis.setTotalCount(dailyStatis.getFailedCount() + dailyStatis.getSuccessCount()
								+ dailyStatis.getTimeoutCount() + dailyStatis.getUnkownCount());
						dailyStatisList.add(dailyStatis);
					}
				});
			});
            List<BalanceModifyVo> modifyVos=new ArrayList<>();
            List<ReturnBalanceMessage> returnBalanceMessages=new ArrayList<>();
			if (!CollectionUtils.isEmpty(dailyStatisList)) {
				BulkOperations ops = template.bulkOps(BulkMode.UNORDERED, MessageDailyStatis.class);
				for (MessageDailyStatis statisData : dailyStatisList) {
					Date beginTime = DateTimeUtil.getBeginTimeOfDate(statisData.getStatisTime());
					Date endTime = DateTimeUtil.getEndTimeOfDate(statisData.getStatisTime());
					Query query = Query.query(Criteria.where("template_id").is(statisData.getTemplateId())
							.and("statisTime").gte(beginTime).lte(endTime));
					if (template.exists(query, MessageDailyStatis.class)) {
                        List<MessageDailyStatis> messageDailyStatis = template.find(query, MessageDailyStatis.class);
                        if(!CollectionUtils.isEmpty(messageDailyStatis)){
                            MessageDailyStatis statis = messageDailyStatis.get(0);
                          //只有模板是计费模板时才发起失败返还
                            if(statisData.getTemplateType() != null && 1 == statisData.getTemplateType().intValue()) {
	                            Long timeoutCost = statis.getTimeoutCost();
	                            Long failedCost = statis.getFailedCost();
	                            ReturnBalanceMessage balanceMessage=new ReturnBalanceMessage();
	                            BalanceModifyVo vo=new BalanceModifyVo();
	                            vo.setAccountNo(statis.getAdvertiserAccount());
	                            vo.setChannel(3);
	                            vo.setCommitDate(DateTimeUtil.getCurrentDateTime());
	                            vo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
	                            AtomicLong totalCost = new AtomicLong();
	                            if(statisData.getFailedCost()-failedCost>0){
	                                balanceMessage.setFailedCost(statisData.getFailedCost()-failedCost);
	                                totalCost.addAndGet(statisData.getFailedCost()-failedCost);
	                            }
	                            if(statisData.getTimeoutCost()-timeoutCost>0){
	                                balanceMessage.setTimeoutCost(statisData.getTimeoutCost()-timeoutCost);
	                                totalCost.addAndGet(statisData.getTimeoutCost()-timeoutCost);
	                            }
								failedAndTimeoutReturn(modifyVos, returnBalanceMessages, balanceMessage, vo, totalCost);
                            }
						}
						ops.updateOne(query,
								new Update().set("unkown_count", statisData.getUnkownCount())
										.set("success_count", statisData.getSuccessCount())
										.set("failed_count", statisData.getFailedCount())
										.set("total_count", statisData.getTotalCount())
										.set("timeout_count",statisData.getTimeoutCount())
										.set("unkown_cost", statisData.getUnkownCost())
										.set("success_cost", statisData.getSuccessCost())
										.set("failed_cost", statisData.getFailedCost())
										.set("timeoutCost", statisData.getTimeoutCost())
										.set("statisTime",statisData.getStatisTime())
										.set("template_subject",statisData.getTemplateSubject()));
					} else {
						//只有模板是计费模板时才发起失败返还
                        if(statisData.getTemplateType() != null && 1 == statisData.getTemplateType().intValue()) {
							ReturnBalanceMessage balanceMessage=new ReturnBalanceMessage();
	                        balanceMessage.setReturnTime(DateTimeUtil.getCurrentDateTime());
	                        balanceMessage.setAdvertiserAccount(statisData.getAdvertiserAccount());
	                        BalanceModifyVo vo=new BalanceModifyVo();
	                        vo.setAccountNo(statisData.getAdvertiserAccount());
	                        vo.setChannel(3);
	                        vo.setCommitDate(DateTimeUtil.getCurrentDateTime());
	                        vo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
	                        AtomicLong totalCost = new AtomicLong();
	                        if(statisData.getFailedCount()>0){
	                            balanceMessage.setFailedCost(statisData.getFailedCost());
	                            totalCost.addAndGet(statisData.getFailedCount());
	                        }
	                        if(statisData.getTimeoutCost()>0){
	                            balanceMessage.setTimeoutCost(statisData.getTimeoutCost());
	                            totalCost.addAndGet(statisData.getTimeoutCost());
	                        }
							failedAndTimeoutReturn(modifyVos, returnBalanceMessages, balanceMessage, vo, totalCost);
                        }
						ops.insert(statisData);
					}
				}
				ops.execute();
			}
            if(!CollectionUtils.isEmpty(returnBalanceMessages)){
                returnBalanceMessageService.save(returnBalanceMessages);
                costOffsetServiceImpl.sendBalanceAdjustequest(modifyVos);
            }
			long costTime = System.currentTimeMillis() - startTime;
			log.info(String.format("async static data : %tc successed and cost time is : %d", date, costTime));
		} catch (Exception e) {
			log.error(String.format("async static data : %tF failed", date), e);
		} finally {
			try {
				if (lock != null && lock.lock()) {
					lock.unlock();
				}
			} catch (Exception fe) {

			}
		}
	}

	private void failedAndTimeoutReturn(List<BalanceModifyVo> modifyVos, List<ReturnBalanceMessage> returnBalanceMessages, ReturnBalanceMessage balanceMessage, BalanceModifyVo vo, AtomicLong totalCost) {
		if(balanceMessage.getFailedCost()>0
				|| balanceMessage.getTimeoutCost()>0){
			returnBalanceMessages.add(balanceMessage);
			BigDecimal cost = new BigDecimal(totalCost.get()).divide(new BigDecimal(10000));
			vo.setAmount(cost);
			modifyVos.add(vo);
		}
	}

	/**
	 * realtime statis iterator 15 minutes
	 */
	@Override
	@Async
	public void realTimeStatis() {
		Date date = DateTimeUtil.getCurrentDateTime();
		// 统计不能并行执行，每次只能跑一个
		RedisLock lock = new RedisLock(valueOperations, keyUtils.getKey("advert-sms-realstatis"), realTimeWaitTimeoutMillisecond,
				exprireMillisecond);
		try {
			if (!lock.lock()) {
				log.info("can't fetch data statis locker!");
				return;
			}
			Map<Integer, List<MessageCountVo>> mappedData = realTimeStatisData();
			List<MessageDailyStatis> dailyStatisList = new ArrayList<>();
			mappedData.forEach((templateId, voList) -> {
				MessageDailyStatis dailyStatis = null;
				for (MessageCountVo countVo : voList) {
					if (null == dailyStatis) {
						dailyStatis = new MessageDailyStatis();
					}
					if(countVo.getTemplateId().equals(dailyStatis.getTemplateId())){
						dailyStatis.setTemplateSubject(countVo.getTemplateSubject());
						Integer counter = countVo.getCounter();
						Long cost = countVo.getCost();
						if (MessageStatusType.FAILED.getValue().equals(countVo.getMsgStatus())) {
							Integer failedCount = dailyStatis.getFailedCount();
							dailyStatis.setFailedCount(failedCount+counter);
							dailyStatis.setFailedCost(cost);
						} else {
							dailyStatisUpdate(dailyStatis, countVo, counter, cost);
						}
					}else{
						BeanUtils.copyProperties(countVo, dailyStatis, "status", "counter");
						dailyStatisSave(dailyStatis, countVo);
					}
				}
				if(null!=dailyStatis){
					dailyStatis.setTotalCount(dailyStatis.getFailedCount() + dailyStatis.getSuccessCount()
							+ dailyStatis.getTimeoutCount() + dailyStatis.getUnkownCount());
					dailyStatisList.add(dailyStatis);
				}
			});
            List<BalanceModifyVo> modifyVos=new ArrayList<>();
			List<ReturnBalanceMessage> returnBalanceMessages=new ArrayList<>();
			if (!CollectionUtils.isEmpty(dailyStatisList)) {
				BulkOperations ops = template.bulkOps(BulkMode.UNORDERED, MessageDailyStatis.class);
				for (MessageDailyStatis statisData : dailyStatisList) {
					Date beginTime = DateTimeUtil.getBeginTimeOfDate(statisData.getStatisTime());
					Date endTime = DateTimeUtil.getEndTimeOfDate(statisData.getStatisTime());
					Query query = Query.query(Criteria.where("template_id").is(statisData.getTemplateId())
							.and("statisTime").gte(beginTime).lte(endTime));
                    if (template.exists(query, MessageDailyStatis.class)) {
                        List<MessageDailyStatis> messageDailyStatis = template.find(query, MessageDailyStatis.class);
                        if(!CollectionUtils.isEmpty(messageDailyStatis)){
                            MessageDailyStatis statis = messageDailyStatis.get(0);
                            Long timeoutCost = statis.getTimeoutCost();
                            Long failedCost = statis.getFailedCost();
                            ReturnBalanceMessage balanceMessage=new ReturnBalanceMessage();
                            balanceMessage.setAdvertiserAccount(statis.getAdvertiserAccount());
                            balanceMessage.setReturnTime(DateTimeUtil.getCurrentDateTime());
                            BalanceModifyVo vo=new BalanceModifyVo();
                            vo.setAccountNo(statis.getAdvertiserAccount());
                            vo.setChannel(3);
                            vo.setCommitDate(DateTimeUtil.getCurrentDateTime());
                            vo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
                            AtomicLong totalCost = new AtomicLong();
                            if(statisData.getFailedCost()-failedCost>0){
                                balanceMessage.setFailedCost(statisData.getFailedCost()-failedCost);
                                totalCost.addAndGet(statisData.getFailedCost()-failedCost);
                            }
                            if(statisData.getTimeoutCost()-timeoutCost>0){
                                balanceMessage.setTimeoutCost(statisData.getTimeoutCost()-timeoutCost);
                                totalCost.addAndGet(statisData.getTimeoutCost()-timeoutCost);
                            }
                            if(statisData.getTemplateType() != null && 1 == statisData.getTemplateType().intValue()) {
                            	failedAndTimeoutReturn(modifyVos, returnBalanceMessages, balanceMessage, vo, totalCost);
                            }
						}
						ops.updateOne(query,
								new Update().set("unkown_count", statisData.getUnkownCount())
										.set("success_count", statisData.getSuccessCount())
										.set("failed_count", statisData.getFailedCount())
										.set("total_count", statisData.getTotalCount())
										.set("timeout_count",statisData.getTimeoutCount())
										.set("unkown_cost", statisData.getUnkownCost())
										.set("success_cost", statisData.getSuccessCost())
										.set("failed_cost", statisData.getFailedCost())
										.set("timeoutCost", statisData.getTimeoutCost())
										.set("statis_time",statisData.getStatisTime())
										.set("template_subject",statisData.getTemplateSubject()));
					} else {
						if(statisData.getTemplateType() != null && 1 == statisData.getTemplateType().intValue()) {
	                        ReturnBalanceMessage balanceMessage=new ReturnBalanceMessage();
	                        balanceMessage.setReturnTime(DateTimeUtil.getCurrentDateTime());
	                        balanceMessage.setAdvertiserAccount(statisData.getAdvertiserAccount());
	                        BalanceModifyVo vo=new BalanceModifyVo();
	                        vo.setAccountNo(statisData.getAdvertiserAccount());
	                        vo.setChannel(3);
	                        vo.setCommitDate(DateTimeUtil.getCurrentDateTime());
	                        vo.setTransId(SerialNoGenerator.generateBatchFeeDeduction());
	                        AtomicLong totalCost = new AtomicLong();
	                        if(statisData.getFailedCount()>0){
	                            balanceMessage.setFailedCost(statisData.getFailedCost());
	                            totalCost.addAndGet(statisData.getFailedCount());
	                        }
	                        if(statisData.getTimeoutCost()>0){
	                            balanceMessage.setTimeoutCost(statisData.getTimeoutCost());
	                            totalCost.addAndGet(statisData.getTimeoutCost());
	                        }
							failedAndTimeoutReturn(modifyVos, returnBalanceMessages, balanceMessage, vo, totalCost);
						}
						ops.insert(statisData);
					}
				}
				ops.execute();
			}
            if(!CollectionUtils.isEmpty(returnBalanceMessages)){
                returnBalanceMessageService.save(returnBalanceMessages);
                costOffsetServiceImpl.sendBalanceAdjustequest(modifyVos);
            }
			long costTime = System.currentTimeMillis() - date.getTime();
			log.info(String.format("async static data : %tc successed and cost time is : %d", date, costTime));
		} catch (Exception e) {
			log.error(String.format("async static data : %tF failed", date), e);
		} finally {
			try {
				if (lock != null && lock.lock()) {
					lock.unlock();
				}
			} catch (Exception fe) {

			}
		}
	}

	private void dailyStatisSave(MessageDailyStatis dailyStatis, MessageCountVo countVo) {
		if (MessageStatusType.FAILED.getValue().equals(countVo.getMsgStatus())) {
			dailyStatis.setFailedCount(countVo.getCounter());
			dailyStatis.setFailedCost(countVo.getCost());
		} else if (MessageStatusType.SUCCESS.getValue().equals(countVo.getMsgStatus())) {
			dailyStatis.setSuccessCount(countVo.getCounter());
			dailyStatis.setSuccessCost(countVo.getCost());
		} else if (MessageStatusType.TIME_OUT.getValue().equals(countVo.getMsgStatus())) {
			dailyStatis.setTimeoutCount(countVo.getCounter());
			dailyStatis.setTimeoutCost(countVo.getCost());
		} else {
			dailyStatis.setUnkownCount(countVo.getCounter());
			dailyStatis.setUnkownCost(countVo.getCost());
		}
	}

	private void dailyStatisUpdate(MessageDailyStatis dailyStatis, MessageCountVo countVo, Integer counter, Long cost) {
		if (MessageStatusType.SUCCESS.getValue().equals(countVo.getMsgStatus())) {
			Integer successCount = dailyStatis.getSuccessCount();
			dailyStatis.setSuccessCount(successCount+counter);
			dailyStatis.setSuccessCost(cost);
		} else if (MessageStatusType.TIME_OUT.getValue().equals(countVo.getMsgStatus())) {
			Integer timeoutCount = dailyStatis.getTimeoutCount();
			dailyStatis.setTimeoutCount(timeoutCount+counter);
			dailyStatis.setTimeoutCost(cost);
		} else {
			Integer unkownCount = dailyStatis.getUnkownCount();
			dailyStatis.setUnkownCount(unkownCount+counter);
			dailyStatis.setUnkownCost(cost);
		}
	}


	/**
	 * counter three status messages counter by template_id.
	 * 
	 * @param date           search time
	 * @return List<MessageCountVo>
	 */
	private Map<Integer, Map<Date, List<MessageCountVo>>> statisMessage(Date date, int days) {
		List<MessageCountVo> allVos = new ArrayList<>();
		for (int i = 0; i < days; i++) {
			// 統計連續三天的數據
			Date startTime = DateTimeUtil.getBeginTimeOfDate(date);
			Date start = Date.from(Instant.ofEpochMilli(startTime.getTime()).minus(Duration.ofDays(i)));
			Date endTime = DateTimeUtil.getEndTimeOfDate(date);
			Date end = Date.from(Instant.ofEpochMilli(endTime.getTime()).minus(Duration.ofDays(i)));
			String collectionName = MessageDocumentNameUtil.getMessageDocumentname(start);
			String[] groupFields = new String[] { "flux_account", "flux_name", "advertiser_account", "advertiser_name",
					"convert_flg", "template_id", "template_subject", "msg_status","template_type"};
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(Criteria.where("create_time").gte(start).lte(end)),
					Aggregation.group(groupFields).count().as("counter").sum("cost").as("cost")// ,
			// Aggregation.sort(Direction.ASC, "counter", "template_id")
			);
			List<MessageCountVo> vos = this.template.aggregate(aggregation, collectionName, MessageCountVo.class)
					.getMappedResults();
			vos.forEach((vo) -> {
				vo.setStatisTime(start);
			});
			allVos.addAll(vos);
		}
		Map<Integer, Map<Date, List<MessageCountVo>>> messagesByIdDate = allVos.stream().collect(Collectors
				.groupingBy(MessageCountVo::getTemplateId, Collectors.groupingBy(MessageCountVo::getStatisTime)));
		return messagesByIdDate;
	}
	
	/**
	 * 實時統計當天數據
	 * @return Map<Integer, List<MessageCountVo>>
	 */
	private Map<Integer, List<MessageCountVo>> realTimeStatisData(){
		Date date = DateTimeUtil.getCurrentDateTime();
		Date startTime = DateTimeUtil.getBeginTimeOfDate(date);
		Date endTime = DateTimeUtil.getEndTimeOfDate(date);
		String collectionName = MessageDocumentNameUtil.getMessageDocumentname(startTime);
		String[] groupFields = new String[] { "flux_account", "flux_name", "advertiser_account", "advertiser_name",
				"convert_flg", "template_id", "template_subject", "msg_status","cost","template_type"};
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("create_time").gte(startTime).lte(endTime)),
				Aggregation.group(groupFields).count().as("counter").sum("cost").as("cost")
				
		);
		List<MessageCountVo> vos = this.template.aggregate(aggregation, collectionName, MessageCountVo.class)
				.getMappedResults();
		vos.forEach((vo)->{
			vo.setStatisTime(date);
		});
		Map<Integer, List<MessageCountVo>> mappedData = vos.stream().collect(Collectors.groupingBy(MessageCountVo::getTemplateId));
		return mappedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chuanglan.mongo.service.service.MessageStatisService#pageQuery(com.
	 * chuanglan.mongo.service.parameter.MessagePageableQueryParameter)
	 */
	@Override
	// @Cacheable(cacheNames="message-static-query",key="#root.targetClass.simpleName+':'+#root.methodName+':'+#parameter.hashCode()")
	public Page<MessageDailyStatisVo> pageQuery(MessagePageableQueryParameter parameter) {
		Criteria criteria = new Criteria();
		criteria.and("statis_time").lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()))
				.gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime()));
		if (parameter.getTemplateId() != null) {
			criteria.and("template_id").is(parameter.getTemplateId());
		}
		if (StringUtils.isNotBlank(parameter.getTemplateSubject())) {
			criteria.and("template_subject").is(parameter.getTemplateSubject());
		}
		if(StringUtils.isNotBlank(parameter.getFluxAccount())) {
			criteria.and("flux_account").is(parameter.getFluxAccount());
		}
		if(StringUtils.isNotBlank(parameter.getFluxName())) {
			criteria.and("flux_name").regex(Pattern.compile(".*?"+parameter.getFluxName()+".*", Pattern.CASE_INSENSITIVE));
		}
		//广告主
		if(StringUtils.isNotBlank(parameter.getAdvertiserAccount())) {
			criteria.and("advertiser_account").in(parameter.getAdvertiserAccount());
		}
		if(StringUtils.isNotBlank(parameter.getAdvertiserName())) {
			criteria.and("advertiser_name").regex(Pattern.compile(".*?"+parameter.getAdvertiserName()+".*", Pattern.CASE_INSENSITIVE));
		}
		Query query = new Query();
		query.with(new Sort(Constants.ORDER_DIRECTION.get(parameter.getAscFlg()), Constants.ORDER_COLUMN.get(parameter.getOrderCol())));
		query.addCriteria(criteria);
		long count = template.count(query, MessageDailyStatis.class);
		Pageable pageable = PageRequest.of(parameter.getPageNumber(), parameter.getPageSize());
		query.with(pageable);
		List<MessageDailyStatis> messages = template.find(query, MessageDailyStatis.class);

		List<MessageDailyStatisVo> vos = new ArrayList<>();
		messages.forEach((message) -> {
			MessageDailyStatisVo vo = new MessageDailyStatisVo();
			BeanUtils.copyProperties(message, vo, "id","template_type");
			vos.add(vo);
		});
		Page<MessageDailyStatisVo> pageList = new PageImpl<>(vos, pageable, count);
		return pageList;
	}

	
	@Override
	public List<TemplateSendStatusVo> queryTemplateSendStatus(DynamicStaitsQueryParameter parameter){
		if(DynamicStaitsQueryType.TIME.getType().equals(parameter.getQueryType())){
			Criteria criteria = Criteria.where("statis_time").
					gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime())).lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()));
			if(!CollectionUtils.isEmpty(parameter.getTemplateIds())){
				criteria.and("template_id").in(parameter.getTemplateIds());
			}
			if(StringUtils.isNotBlank(parameter.getAdvertiserName())){
				criteria.and("advertiser_name").regex(Pattern.compile(".*?"+parameter.getAdvertiserName()+".*", Pattern.CASE_INSENSITIVE));
			}
			if(StringUtils.isNotBlank(parameter.getFluxName())){
				criteria.and("flux_name").regex(Pattern.compile(".*?"+parameter.getFluxName()+".*", Pattern.CASE_INSENSITIVE));
			}
			if(StringUtils.isNotBlank(parameter.getTemplateSubject())){
				criteria.and("template_subject").is(parameter.getTemplateSubject());
			}
			if(StringUtils.isNotBlank(parameter.getFluxAccount())){
				criteria.and("flux_account").is(parameter.getFluxAccount());
			}
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.group().sum("unkown_count").as("unkownCount")
							.sum("success_count").as("successCount")
							.sum("failed_count").as("failedCount").sum("timeout_count").as("timeoutCount").sum("total_count").as("totalCount")
							.max("statis_time").as("statisTime"));
			List<TemplateSendStatusVo> list = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendStatusVo.class).getMappedResults();
			return list;
		}else if(DynamicStaitsQueryType.TEMPLATE.getType().equals(parameter.getQueryType())) {
			Criteria criteria = Criteria.where("template_id").in(parameter.getTemplateIds());
			if(null != parameter.getStartTime() && null != parameter.getEndTime()) {
				criteria = criteria.and("statis_time").gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime())).lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()));
			}
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.group("template_id").first("template_id").as("templateId").sum("unkown_count").as("unkownCount")
					.sum("success_count").as("successCount")
					.sum("failed_count").as("failedCount").sum("timeout_count").as("timeoutCount").sum("total_count").as("totalCount")
				);
			List<TemplateSendStatusVo> list = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendStatusVo.class).getMappedResults();
			return list;
		}else if(DynamicStaitsQueryType.ADVERTISER.getType().equals(parameter.getQueryType())) {
			Criteria criteria = Criteria.where("advertiser_account").is(parameter.getAdvertiserAccount());
			if(null != parameter.getStartTime() && null != parameter.getEndTime()) {
				criteria = criteria.and("statis_time").gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime())).lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()));
			}
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.group("advertiser_account").first("advertiser_account").as("advertiserAccount").sum("unkown_count").as("unkownCount")
					.sum("success_count").as("successCount")
					.sum("failed_count").as("failedCount").sum("timeout_count").as("timeoutCount").sum("total_count").as("totalCount")
				);
			List<TemplateSendStatusVo> list = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendStatusVo.class).getMappedResults();
			return list;
		}else {
			Criteria criteria = Criteria.where("flux_account").is(parameter.getFluxAccount());
			if(null != parameter.getStartTime() && null != parameter.getEndTime()) {
				criteria = criteria.and("statis_time").gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime())).lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()));
			}
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.group("flux_account").first("flux_account").as("fluxAccount").sum("unkown_count").as("unkownCount")
							.sum("success_count").as("successCount")
							.sum("failed_count").as("failedCount").sum("timeout_count").as("timeoutCount").sum("total_count").as("totalCount")
			);
			List<TemplateSendStatusVo> list = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendStatusVo.class).getMappedResults();
			return list;
		}
	}

	@Override
	public List<String> queryStatisFluxAccount(MessagePageableQueryParameter parameter) {
		Criteria criteria = new Criteria();
		criteria.and("statis_time").lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()))
				.gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime()));
		if (parameter.getTemplateId() != null) {
			criteria.and("template_id").is(parameter.getTemplateId());
		}
		if (StringUtils.isNotBlank(parameter.getTemplateSubject())) {
			criteria.and("template_subject").is(parameter.getTemplateSubject());
		}
		if(StringUtils.isNotBlank(parameter.getFluxAccount())) {
			criteria.and("flux_account").is(parameter.getFluxAccount());
		}
		if(StringUtils.isNotBlank(parameter.getFluxName())) {
			criteria.and("flux_name").regex(Pattern.compile(".*"+parameter.getFluxName()+".*$", Pattern.CASE_INSENSITIVE));
		}
		//广告主
		if(StringUtils.isNotBlank(parameter.getAdvertiserAccount())) {
			criteria.and("advertiser_account").in(parameter.getAdvertiserAccount());
		}
		if(StringUtils.isNotBlank(parameter.getAdvertiserName())) {
			criteria.and("advertiser_name").regex(Pattern.compile(".*"+parameter.getAdvertiserName()+".*$", Pattern.CASE_INSENSITIVE));
		}

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.group("flux_account").first("flux_account").as("fluxAccount"));
		List<JSONObject> message_daily_statis = this.template.aggregate(aggregation, "message_daily_statis", JSONObject.class).getMappedResults();
		List<String> strings=new ArrayList<>();
		for(JSONObject jsonObject : message_daily_statis){
			strings.add((String) jsonObject.get("fluxAccount"));
		}

		return strings;
	}


	@Override
	public List<TemplateSendCountResp> queryTemplateSuccessCount(List<Integer> ids){
		Criteria criteria = new Criteria();
		criteria.and("template_id").in(ids);
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.group("template_id").first("template_id").as("templateId").sum("success_count").as("successCount")
		);
		List<TemplateSendCountResp> messageDailyStatis = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendCountResp.class).getMappedResults();
		return messageDailyStatis;
	}



}

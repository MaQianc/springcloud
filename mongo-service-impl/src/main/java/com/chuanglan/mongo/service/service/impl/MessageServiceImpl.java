/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.chuanglan.mongo.service.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.documents.CommonMessage;
import com.chuanglan.mongo.service.parameter.MessagePageableQueryParameter;
import com.chuanglan.mongo.service.parameter.MessageParamter;
import com.chuanglan.mongo.service.parameter.MsgStatusUpdateParamter;
import com.chuanglan.mongo.service.service.MessageService;
import com.chuanglan.mongo.service.util.MessageDocumentNameUtil;
import com.chuanglan.mongo.service.vo.MessageVo;
import com.mongodb.bulk.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Bomb create-time 2019-01-09 20:31:19
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MongoTemplate template;

	private Set<String> collectionSet = new HashSet<>();
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chuanglan.mongo.service.service.MessageService#save(java.util.List)
	 */
	//@Transactional(rollbackFor=Exception.class)
	@Override
	// @CacheEvict(cacheNames="message-query",key="MessageServiceImpl:pageQuery:*")
	public int save(List<MessageParamter> messages) throws Exception{
		Date curTime = DateTimeUtil.getCurrentDateTime();
        List<CommonMessage> documents = new ArrayList<>();
        messages.forEach((parameter)->{
        	CommonMessage document = new CommonMessage();
        	BeanUtils.copyProperties(parameter, document);
        	document.setCreateTime(curTime);
        	document.setUpdateTime(curTime);
        	documents.add(document);
        });
        String collectionName = MessageDocumentNameUtil.getMessageDocumentname(curTime);
        createCollection(collectionName);
        //Collection<CommonMessage> insertedDocuments = this.template.insert(documents, collectionName);
        BulkOperations bulkOp = this.template.bulkOps(BulkMode.UNORDERED, collectionName);
        bulkOp.insert(documents);
        BulkWriteResult result = bulkOp.execute();
        if(log.isDebugEnabled()) {
        	long costTime = System.currentTimeMillis()-curTime.getTime();
        	log.debug(String.format("insert %d s record cost millseconds: %d", messages.size(),costTime));
        }
        return result.getInsertedCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chuanglan.mongo.service.service.MessageService#updateStatus(java.util.
	 * List)
	 */
	//@Transactional(rollbackFor=Exception.class)
	@Override
	// @CacheEvict(cacheNames="message-query",key="MessageServiceImpl:pageQuery:*")
	public int updateStatus(List<MsgStatusUpdateParamter> statusParameters) {
		Date curTime = DateTimeUtil.getCurrentDateTime();
		String collectionName = MessageDocumentNameUtil.getMessageDocumentname(curTime);
		BulkOperations bulkOp = this.template.bulkOps(BulkMode.UNORDERED, collectionName);
		for(MsgStatusUpdateParamter parameter : statusParameters) {
			bulkOp.updateOne(Query.query(Criteria.where("msg_id").is(parameter.getMsgId())), Update.update("msg_status", parameter.getStatus()).set("update_time", curTime));
		}
		Integer updateCount = bulkOp.execute().getModifiedCount();
		if(updateCount < statusParameters.size()) {
			Date preMonthTime = new DateTime(curTime.getTime()).plusMonths(1).toDate();
			String preMonthCollectionName = MessageDocumentNameUtil.getMessageDocumentname(preMonthTime);
			if(this.template.collectionExists(preMonthCollectionName)) {
				BulkOperations preBulkOp = this.template.bulkOps(BulkMode.UNORDERED, preMonthCollectionName);
				for(MsgStatusUpdateParamter parameter : statusParameters) {
					bulkOp.updateOne(Query.query(Criteria.where("msg_id").is(parameter.getMsgId())), Update.update("msg_status", parameter.getStatus()).set("update_time", curTime));
				}
				updateCount += preBulkOp.execute().getModifiedCount();
			}
		}
		return updateCount;
	}

	@Override
	// @Cacheable(cacheNames="message-query",key="#root.targetClass.simpleName+':'+#root.methodName+':'+#parameter.hashCode()")
	public Page<MessageVo> pageQuery(MessagePageableQueryParameter parameter) {
		String documentName = MessageDocumentNameUtil.getMessageDocumentname(parameter.getStartTime());
		Criteria criteria = new Criteria();
		criteria.and("create_time").lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()))
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
			criteria.and("flux_name").regex(Pattern.compile("^"+parameter.getFluxName()+".*$", Pattern.CASE_INSENSITIVE));
		}
		if(StringUtils.isNotBlank(parameter.getAdvertiserAccount())) {
			criteria.and("advertiser_account").is(parameter.getAdvertiserAccount());
		}
		if(StringUtils.isNotBlank(parameter.getAdvertiserName())) {
			criteria.and("advertiser_name").regex(Pattern.compile("^"+parameter.getAdvertiserName()+".*$", Pattern.CASE_INSENSITIVE));
		}
		Query query = new Query();
		query.with(new Sort(Direction.DESC, "create_time"));
		query.addCriteria(criteria);
		long count = template.count(query, documentName);
		Pageable pageable = PageRequest.of(parameter.getPageNumber(), parameter.getPageSize());
		query.with(pageable);
		List<CommonMessage> messages = template.find(query, CommonMessage.class, documentName);
		List<MessageVo> vos = new ArrayList<>();
		messages.forEach((message) -> {
			MessageVo vo = new MessageVo();
			BeanUtils.copyProperties(message, vo, "id","template_type");
			vos.add(vo);
		});
		Page<MessageVo> pageList = new PageImpl<>(vos, pageable, count);
		return pageList;
	}
	
	private void createCollection(String collectionName) {
		if(collectionSet.contains(collectionName)) {
			return;
		}
		if(!this.template.collectionExists(collectionName)) {
			try {
				this.template.createCollection(collectionName, CollectionOptions.empty());
				template.indexOps(collectionName).ensureIndex(new Index().on("flux_account", Direction.ASC));
				template.indexOps(collectionName).ensureIndex(new Index().on("advertiser_account", Direction.ASC));
				template.indexOps(collectionName).ensureIndex(new Index().on("create_time", Direction.DESC));
			}catch(Exception e) {
				log.info("create collection failed,name is : "+ collectionName,e);
			}
		}
		this.collectionSet.add(collectionName);
	}

}

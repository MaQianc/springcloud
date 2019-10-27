/**
 * 
 */
package com.chuanglan.mongo.service.service.impl;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.chuanglan.mongo.service.documents.DataMatch;
import com.chuanglan.mongo.service.parameter.PhoneMatchParameter;
import com.chuanglan.mongo.service.parameter.PhoneMatchUploadParameter;
import com.chuanglan.mongo.service.service.DataMatchService;
import com.chuanglan.mongo.service.util.MessageDocumentNameUtil;
import com.mongodb.bulk.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ChuangLan
 *
 */
@Service
@Slf4j
public class DataMatchServiceImpl implements DataMatchService {
	
	@Autowired
	private MongoTemplate template;
	
	/**
	 * 存放数据包的集合名称
	 */
	private CopyOnWriteArraySet<String> collectionNameSet = new CopyOnWriteArraySet<>();
	
	private ReentrantLock locker  = new ReentrantLock();
	
	/**
	 * 校验集合名字，如果不存在需要新建
	 * @param collectionName collection name
	 */
	private void validateCollection(String collectionName) {
		if(collectionNameSet.contains(collectionName)) {
			return;
		}
		boolean result = false;
		try {
			result = locker.tryLock(500, TimeUnit.MILLISECONDS);
		if(!this.template.collectionExists(collectionName) && result) {
			//创建hash索引
			this.template.createCollection(collectionName, CollectionOptions.empty());
			Index index = new CompoundIndexDefinition(new Document("encrypt_data", "hashed"));
			template.indexOps(collectionName).ensureIndex(index);
		}
		}catch(Exception e) {
			log.info("create collection failed,name is : "+ collectionName,e);
		}finally {
			if(result) {
				locker.unlock();
			}
		}
		this.collectionNameSet.add(collectionName);
	}
	

	/* (non-Javadoc)
	 * @see com.chuanglan.mongo.service.service.DataMatchService#uploadMatchData(com.chuanglan.mongo.service.parameter.PhoneMatchUploadParameter)
	 */
	@Override
	public Optional<Integer> uploadMatchData(PhoneMatchUploadParameter uploadParameter) {
		String collectionName = MessageDocumentNameUtil.getMatchDataPackageCollectionName(uploadParameter.getBatchNo());
		validateCollection(collectionName);
		final BulkOperations bulkOp = this.template.bulkOps(BulkMode.UNORDERED, collectionName);
		uploadParameter.getMobiles().forEach(data->{
			bulkOp.insert(new DataMatch(data));
		});
		BulkWriteResult result = bulkOp.execute();		
		return Optional.of(result.getInsertedCount());
	}

	/* (non-Javadoc)
	 * @see com.chuanglan.mongo.service.service.DataMatchService#isMobileMatch(com.chuanglan.mongo.service.parameter.PhoneMatchParameter)
	 */
	@Override
	public Optional<Boolean> isMobileMatch(PhoneMatchParameter matchParameter) {
		String collectionName = MessageDocumentNameUtil.getMatchDataPackageCollectionName(matchParameter.getBatchNo());
		DataMatch data = template.findOne(Query.query(Criteria.where("encrypt_data").is(matchParameter.getEncryptMobile())), DataMatch.class, collectionName);
		if(null != data) {
			return Optional.of(Boolean.TRUE);
		}
		return Optional.of(Boolean.FALSE);
	}

}

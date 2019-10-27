/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chuanglan.mongo.service.documents.MessageDailyStatis;

/**
 * @author      Bomb
 * create-time  2019-01-17 14:07:10
 */
@Repository
public interface MessageDailyStatisRepository extends MongoRepository<MessageDailyStatis, String> {

}

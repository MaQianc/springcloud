/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.actuator.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

/**
 * @author      Bomb
 * create-time  2019-01-08 15:04:29
 */
@Configuration
@Endpoint(id="mongo-endpoint")
public class MongoEndpoint {

	@Autowired
	private MongoClient client;

	@ReadOperation
	public Map<String, Object> clientOps(){
		Map<String, Object> data = new HashMap<String, Object>();
		MongoClientOptions clientOptions = client.getMongoClientOptions();
		data.put("client-options", clientOptions.toString());
		data.put("max-bson-size", client.getMaxBsonObjectSize());
		data.put("server-addresses", client.getServerAddressList());
		data.put("Replica-SetStatus", client.getReplicaSetStatus());
		return data;
	}
}

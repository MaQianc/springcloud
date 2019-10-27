package com.chuanglan.mongo.service.service.impl;

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.documents.ReturnBalanceMessage;
import com.chuanglan.mongo.service.service.ReturnBalanceMessageService;
import com.mongodb.bulk.BulkWriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ReturnBalanceMessageServiceImpl implements ReturnBalanceMessageService {

    @Autowired
    private MongoTemplate template;

    /**
     * 添加余额返还记录
     * @param returnBalanceMessages
     * @throws Exception
     */
    @Override
    public void save(List<ReturnBalanceMessage> returnBalanceMessages){
        Date curTime = DateTimeUtil.getCurrentDateTime();
        BulkOperations bulkOp = this.template.bulkOps(BulkOperations.BulkMode.UNORDERED, ReturnBalanceMessage.class);
        bulkOp.insert(returnBalanceMessages);
        BulkWriteResult execute = bulkOp.execute();
        log.info("insert return_balance size %d cost millseconds:",execute.getInsertedCount(),System.currentTimeMillis()-curTime.getTime());
    }



}

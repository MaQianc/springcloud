package com.chuanglan.mongo.service.service;

import com.chuanglan.mongo.service.documents.ReturnBalanceMessage;

import java.util.List;

public interface ReturnBalanceMessageService {


    void save(List<ReturnBalanceMessage> returnBalanceMessages) throws Exception;
}

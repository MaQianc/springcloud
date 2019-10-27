package com.chuanglan.mongo.service.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document("return_balance_message")
public class ReturnBalanceMessage {


    @Id
    private String id;


    @Indexed(name = "advertiser_idx")
    @Field("advertiser_account")
    private String advertiserAccount;

    @Field("failed_cost")
    private Long failedCost=0l;

    @Field("timeout_cost")
    private Long timeoutCost=0l;

    @Field("unknow_cost")
    private Long unknowCost=0l;

    @Indexed(name="return_time_idx")
    @Field("return_time")
    private Date returnTime;



}

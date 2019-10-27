/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * @author      Bomb
 * create-time  2019-01-05 11:47:31
 */
@Data
public class CommonMessage {

	@Id
	private String id;
	
	/**
	 * flux N account.
	 */
	@Indexed(name="flux_idx")
	@Field("flux_account")
	private String fluxAccount;
	
	/**
	 * flux account name.
	 */
	@Field("flux_name")
	private String fluxName;
	
	/**
	 * adverter account.
	 */
	@Indexed(name="advertiser_idx")
	@Field("advertiser_account")
	private String advertiserAccount;
	
	/**
	 * advert customer name
	 */
	@Field("advertiser_name")
	private String advertiserName;
	
	/**
	 * msg mobile.
	 */
	private String mobile;
	
	/**
	 * message append content.
	 */
	private String content;
	
	/**
	 * short url.
	 */
	@Field("short_url")
	private String shortUrl;
	
	/**
	 * message id.
	 */
	@Field("msg_id")
	@Indexed(name="msg_id_idx",unique=true)
	private String msgId;
	
	/**
	 * message status, 0:unkown 1:success 2:failed.3:timeout
	 */
	@Field("msg_status")
	private Integer msgStatus = 0;
	
	/**
	 * create time.
	 */
	@Indexed(name="create_idx",direction=IndexDirection.DESCENDING)
	@Field("create_time")
	private Date createTime;
	
	/**
	 * update time.
	 */
	@Field("update_time")
	private Date updateTime;
	
	/**
	 * convert flag, whether use 52.cn ,0: false,   1: true . 
	 */
	@Field("convert_flg")
	private Integer convertFlg;
	
	/**
	 * advert
	 */
	@Field("template_id")
	private Integer templateId;
	
	/**
	 * template subject
	 */
	@Field("template_subject")
	private String templateSubject;
	
	private Long cost;
	
	@Field("template_type")
	private Integer templateType;
	
}

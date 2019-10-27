/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.chuanglan.advert.common.exception.ExceptionHandle;

/**
 * @author      Bomb
 * create-time  2019-01-08 20:16:18
 */
@Configuration
@ControllerAdvice
public class ExceptionAdvice extends ExceptionHandle {

}

/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */
 
package com.chuanglan.mongo.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author      Bomb
 * create-time  2019-01-05 14:33:38
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	@Value("${info.doc.show}")
    private boolean show;

    @Value("${info.doc.path}")
    private String path;
    
    /**
     * under spring cloud gateway, lbs prefix name.
     * @return Docket
     */
    /*@Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).enable(show).groupName("gateway")
                .apiInfo(apiInfo()).pathMapping(path).select()
                .apis(RequestHandlerSelectors.basePackage("com.dffl.bs.address.rest.impl")).build();
        return docket;
    }*/
    
    /**
     * localhost request path is '/'.
     * @return Docket
     */
    @Bean
    public Docket localapi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).enable(show).groupName("local")
                .apiInfo(apiInfo()).pathMapping("/").select()
                .apis(RequestHandlerSelectors.basePackage("com.chuanglan.mongo.service.controller")).build();
        return docket;
    }

    /**
     * get swagger apiInfo object.
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("mongo-data-service").version("1.0").description("mongo data service interface").build();
    }
}

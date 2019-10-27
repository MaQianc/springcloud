/**
 * 
 */
package com.chuanglan.mongo.service.documents;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * @author ChuangLan
 *
 */
@Data
public class DataMatch {

	@Id
	private String id;
	
	@Field("encrypt_data")
	private String encryptData;
	
	public DataMatch(String encryptData) {
		this.encryptData = encryptData;
	}
}

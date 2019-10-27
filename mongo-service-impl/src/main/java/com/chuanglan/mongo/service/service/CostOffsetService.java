/**
 * 
 */
package com.chuanglan.mongo.service.service;

import java.util.Date;

/**
 * @author ChuangLan
 *
 */
public interface CostOffsetService {

	 Boolean failedOffSet(Date date);
	 
	 Boolean unkownOffSet(Date date);
}

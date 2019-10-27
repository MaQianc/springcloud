/**
 * 
 */
package com.chuanglan.mongo.service.util;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.format.datetime.DateFormatter;

/**
 * @author ChuangLan
 *
 */
public class SerialNoGenerator {

	public static String SERIAL_NO_FORMAT = "yyyyMMddHHmmssSSS";
	
	/**
	 * 生成批量扣费请求的批处理号
	 * @return
	 */
	public static String generateBatchFeeDeduction() {
		DateFormatter format = new DateFormatter(SERIAL_NO_FORMAT);
		String datePrefixStr = format.print(new Date(), Locale.CHINA);
		StringBuilder sb = new StringBuilder(datePrefixStr);
		sb.append(generateRandomNum(15));
		return sb.toString();
	}
	
	/**
	 * 
	 * @param length
	 * @return
	 */
	private static String generateRandomNum(int length) {
		StringBuilder sb = new StringBuilder("");
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for(int i =0;i<length;i++) {
			sb.append(random.nextInt(10));
		}		
		return sb.toString();
	}
	
}

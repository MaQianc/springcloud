/**
 * 
 */
package com.chuanglan.mongo.service.service;

import java.util.Optional;

import com.chuanglan.mongo.service.parameter.PhoneMatchParameter;
import com.chuanglan.mongo.service.parameter.PhoneMatchUploadParameter;

/**
 * @author ChuangLan
 *
 */
public interface DataMatchService {

	/**
	 * 上传一个匹配包
	 * @param uploadParameter PhoneMatchUploadParameter
	 * @return Optional<Integer>
	 */
	Optional<Integer> uploadMatchData(PhoneMatchUploadParameter uploadParameter);
	
	/**
	 * 验证手机号是否匹配成功
	 * @param matchParameter PhoneMatchParameter
	 * @return Optional<Boolean>
	 */
	Optional<Boolean> isMobileMatch(PhoneMatchParameter matchParameter);
}

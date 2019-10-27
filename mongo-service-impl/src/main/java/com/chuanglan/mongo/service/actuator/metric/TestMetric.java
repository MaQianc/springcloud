/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.chuanglan.mongo.service.actuator.metric;

import java.util.HashMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * @author Bomb create-time 2019-01-04 19:08:59
 */
//@Component
public class TestMetric implements MeterBinder {

	public Counter count1;

	public Map<String, Double> map = new HashMap<>();

	@Override
	public void bindTo(MeterRegistry registry) {
		this.count1 = Counter.builder("count1").tag("name", "job1").description("test-job").register(registry);
		this.count1.increment();
		Gauge.builder("test-guage", map, x -> x.get("x")).tag("name", "job1").description("test-guage")
				.register(registry);

	}

}

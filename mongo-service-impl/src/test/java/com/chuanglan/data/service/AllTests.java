package com.chuanglan.data.service;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.chuanglan.data.service.controller.MessageControllerTest;
import com.chuanglan.data.service.controller.MessageStatisControllerTest;
import com.chuanglan.data.service.util.RedisLockerTest;
import com.chuanglan.data.service.util.ThreadLocalContainerUtilTest;

@RunWith(Suite.class)
@SuiteClasses({MessageControllerTest.class,
	MessageStatisControllerTest.class,
	ThreadLocalContainerUtilTest.class,
	RedisLockerTest.class
	})
public class AllTests {

	@AfterClass
	public static void  clean() throws Exception{
		MessageControllerTest.after();
	}
}

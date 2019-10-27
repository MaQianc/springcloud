package com.chuanglan.data.service.util;

import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.chuanglan.advert.common.redis.RedisKeyUtils;
import com.chuanglan.mongo.service.Application;
import com.chuanglan.mongo.service.util.RedisLock;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class RedisLockerTest {
	
	@Autowired
	private ValueOperations<String, Object> valueOperations;
	
	private RedisKeyUtils keyUtils = new RedisKeyUtils();
	
	@PostConstruct
	public void before() {
		keyUtils.setAppName("mongo-service-impl");
	}

	@Test
	public void testRedisLockValueOperationsOfStringObjectStringIntInt() {
		RedisLock locker = new RedisLock(valueOperations, keyUtils.getKey("test-case-key"),20*1000,20*1000);
		TestCase.assertEquals("mongo-service-impl-test-case-key_redis_lock", locker.getLockKey());
	}

	@Test
	public void testGetLockKey() {
		RedisLock locker = new RedisLock(valueOperations, keyUtils.getKey("test-case-key"));
		TestCase.assertEquals("mongo-service-impl-test-case-key_redis_lock", locker.getLockKey());
	}

	@Test
	public void testLock() throws Exception{
		final CountDownLatch latch = new CountDownLatch(3);
		RedisLock locker0 = new RedisLock(valueOperations, keyUtils.getKey("test-case-key"));
		RedisLock locker1 = new RedisLock(valueOperations, keyUtils.getKey("test-case-key"));
		RedisLock locker2 = new RedisLock(valueOperations, keyUtils.getKey("test-case-key"));
		final RedisLock[] lockers = new RedisLock[3];
		lockers[0]=locker0;
		lockers[1]=locker1;
		lockers[2]=locker2;
		for(int i=0;i<3;i++) {
			final int j = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						boolean result = lockers[j].lock();
						if(result) {
							Thread.sleep(6000);
							lockers[j].unlock();
						}
					}catch(Exception e) {
						e.printStackTrace();
						TestCase.assertNull(e);
					}
					latch.countDown();
				}
			}).start();
		}
		latch.await();
		TestCase.assertEquals(0, latch.getCount());
	}


}

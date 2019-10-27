/**
 * Copyright (C) 2011-2018 www.253.com Inc. All rights reserved.
  * 注意：本内容仅限于上海创蓝文化传播有限公司内部传阅，禁止外泄以及用于其他的商业目.
 */

package com.chuanglan.mongo.service.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ValueOperations;

/**
 * @author Bomb create-time 2019-01-23 10:40:04
 */
public class RedisLock {

	/**
	 * valueOperations.
	 */
	private ValueOperations<String, Object> valueOperations;
	/**
	 * 重试时间
	 */
	private static final int DEFAULT_ACQUIRY_RETRY_MILLIS = 100;
	/**
	 * 锁的后缀
	 */
	private static final String LOCK_SUFFIX = "_redis_lock";
	/**
	 * 锁的key
	 */
	private String lockKey;
	/**
	 * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
	 */
	private int expireMsecs = 60 * 1000;
	/**
	 * 线程获取锁的等待时间
	 */
	private int timeoutMsecs = 10 * 1000;
	/**
	 * 是否锁定标志
	 */
	private volatile boolean locked = false;

	/**
	 * 构造器
	 * 
	 * @param redisTemplate
	 * @param lockKey       锁的key
	 */
	public RedisLock(ValueOperations<String, Object> valueOperations, String lockKey) {
		this.valueOperations = valueOperations;
		this.lockKey = lockKey + LOCK_SUFFIX;
	}

	/**
	 * 构造器
	 * 
	 * @param redisTemplate
	 * @param lockKey       锁的key
	 * @param timeoutMsecs  获取锁的超时时间
	 */
	public RedisLock(ValueOperations<String, Object> valueOperations, String lockKey, int timeoutMsecs) {
		this(valueOperations, lockKey);
		this.timeoutMsecs = timeoutMsecs;
	}

	/**
	 * 构造器
	 * 
	 * @param redisTemplate
	 * @param lockKey       锁的key
	 * @param timeoutMsecs  获取锁的超时时间
	 * @param expireMsecs   锁的有效期
	 */
	public RedisLock(ValueOperations<String, Object> valueOperations, String lockKey, int timeoutMsecs, int expireMsecs) {
		this(valueOperations, lockKey, timeoutMsecs);
		this.expireMsecs = expireMsecs;
	}

	public String getLockKey() {
		return lockKey;
	}

	/**
	 * 封装和jedis方法
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean setNX(final String key, final String value) {
		return valueOperations.setIfAbsent(key, value, expireMsecs, TimeUnit.MILLISECONDS);
	}

	/**
	 * 获取锁
	 * 
	 * @return 获取锁成功返回ture，超时返回false
	 * @throws InterruptedException
	 */
	public  boolean lock() throws InterruptedException {
		int timeout = timeoutMsecs;
		while (timeout >= 0) {
			long expires = System.currentTimeMillis() + expireMsecs + 1;
			String expiresStr = String.valueOf(expires); // 锁到期时间
			if (this.setNX(lockKey, expiresStr)) {
				locked = true;
				return true;
			}
			timeout -= DEFAULT_ACQUIRY_RETRY_MILLIS;
			// 延时
			Thread.sleep(DEFAULT_ACQUIRY_RETRY_MILLIS);
		}
		return false;
	}

	/**
	 * 释放获取到的锁
	 */
	public synchronized void unlock() {
		if (locked) {
			valueOperations.getOperations().delete(lockKey);
			locked = false;
		}
	}

}

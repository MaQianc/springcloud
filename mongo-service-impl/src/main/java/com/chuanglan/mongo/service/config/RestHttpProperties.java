/**
 * 
 */
package com.chuanglan.mongo.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChuangLan
 * OkHttp properties for game tag request.
 */
@Configuration
@ConfigurationProperties(prefix="rest.http")
public class RestHttpProperties {

	/**
	 * max idle connections.
	 */
	private Integer maxIdleConnections;
	
	/**
	 * keep alive time, unit minute.
	 */
	private Long keepAliveMinutes;
	
	/**
	 * connection timeout.
	 */
	private Long connectionTimeout;
	
	/**
	 * write timeout.
	 */
	private Long writeTimeout;
	
	/**
	 * read timeout.
	 */
	private Long readTimeout;

	/**
	 * @return the maxIdleConnections
	 */
	public Integer getMaxIdleConnections() {
		return maxIdleConnections;
	}

	/**
	 * @param maxIdleConnections the maxIdleConnections to set
	 */
	public void setMaxIdleConnections(Integer maxIdleConnections) {
		this.maxIdleConnections = maxIdleConnections;
	}

	/**
	 * @return the keepAliveMinutes
	 */
	public Long getKeepAliveMinutes() {
		return keepAliveMinutes;
	}

	/**
	 * @param keepAliveMinutes the keepAliveMinutes to set
	 */
	public void setKeepAliveMinutes(Long keepAliveMinutes) {
		this.keepAliveMinutes = keepAliveMinutes;
	}

	/**
	 * @return the connectionTimeout
	 */
	public Long getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @param connectionTimeout the connectionTimeout to set
	 */
	public void setConnectionTimeout(Long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * @return the writeTimeout
	 */
	public Long getWriteTimeout() {
		return writeTimeout;
	}

	/**
	 * @param writeTimeout the writeTimeout to set
	 */
	public void setWriteTimeout(Long writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	/**
	 * @return the readTimeout
	 */
	public Long getReadTimeout() {
		return readTimeout;
	}

	/**
	 * @param readTimeout the readTimeout to set
	 */
	public void setReadTimeout(Long readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Override
	public String toString() {
		return "GameTagHttpProperties [maxIdleConnections=" + maxIdleConnections + ", keepAliveMinutes="
				+ keepAliveMinutes + ", connectionTimeout=" + connectionTimeout + ", writeTimeout=" + writeTimeout
				+ ", readTimeout=" + readTimeout + "]";
	}
	
}

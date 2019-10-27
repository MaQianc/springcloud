/**
 * 
 */
package com.chuanglan.mongo.service.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

/**
 * @author ChuangLan
 *
 */
@Configuration
public class RestConfiguration {
	
	@Bean
	public OkHttpClient getOkHttpClient( RestHttpProperties properties) {
		ConnectionPool pool = new ConnectionPool(properties.getMaxIdleConnections(), properties.getKeepAliveMinutes(),
				TimeUnit.MINUTES);
		List<ConnectionSpec> connectionSpecs = new ArrayList<ConnectionSpec>();
		connectionSpecs.add(ConnectionSpec.CLEARTEXT);
		OkHttpClient client = new OkHttpClient().newBuilder().retryOnConnectionFailure(false)
				.connectionPool(pool).connectionSpecs(connectionSpecs)
				.connectTimeout(Duration.ofMillis(properties.getConnectionTimeout()))
				.writeTimeout(Duration.ofMillis(properties.getWriteTimeout()))
				.readTimeout(Duration.ofMillis(properties.getReadTimeout())).build();
		return client;
	}
	
	@Bean
    public RestTemplate getBalanceServiceTemplate(OkHttpClient client){
        OkHttp3ClientHttpRequestFactory httpRequestFactory = new OkHttp3ClientHttpRequestFactory(client);
        return new RestTemplate(httpRequestFactory);
    }

}

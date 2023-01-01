package com.example.fw.common.dynamodb.config;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBProdIntializer;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB 本番用の設定クラス
 *
 */
@Configuration
@Profile("production")
public class DynamoDBProdConfig {
	@Value("${aws.dynamodb.region:ap-northeast-1}")
	private String regionName;

	
	/**
	 * DynamoDB 初期テーブル作成クラス
	 */	
	@Bean
	public DynamoDBProdIntializer dynamoDBProdIntializer(DynamoDBTableInitializer dynamoDBTableInitializer) {
		return new DynamoDBProdIntializer(dynamoDBTableInitializer);
	}

	
	/**
	 * DynamoDBClient
	 */
	@Bean
	public DynamoDbClient dynamoDbClient() {
		Region region = Region.of(regionName);
		return DynamoDbClient.builder().region(region)
				//個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
				.overrideConfiguration(ClientOverrideConfiguration.builder()
						.addExecutionInterceptor(new TracingInterceptor()).build())
				.build();
	}
	
	/**
	 * DynamoDBEnhancedClient
	 * @return
	 */
	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
		return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient()).build();
	}

	/**
	 * 終了時のDynamoDBClientの接続を切断
	 */
	@PreDestroy
	public void closeDynamoDbEnhancedClient() {
		dynamoDbClient().close();
	}
}

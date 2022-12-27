package com.example.fw.common.dynamodb.config;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
	 * DynamoDBClient
	 */
	@Bean
	public DynamoDbClient dynamoDbClient() {
		Region region = Region.of(regionName);
		return DynamoDbClient.builder().region(region).build();
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

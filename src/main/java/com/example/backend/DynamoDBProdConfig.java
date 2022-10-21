package com.example.backend;

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

	@Bean
	public DynamoDBProdIntializer dynamoDBProdInitializer(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		return new DynamoDBProdIntializer(dynamoDbClient(), dynamoDbEnhancedClient());
	}

	@Bean
	public DynamoDbClient dynamoDbClient() {
		Region region = Region.of(regionName);
		return DynamoDbClient.builder().region(region).build();
	}

	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
		return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient()).build();
	}

	@PreDestroy
	public void closeDynamoDbEnhancedClient() {
		dynamoDbClient().close();
	}
}

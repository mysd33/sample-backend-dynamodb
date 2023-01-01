package com.example.fw.common.dynamodb.config;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBCommonConfig {
	@Autowired
	private DynamoDbClient dynamoDbClient;
		
	/**
	 * DynamoDBEnhancedClient
	 * @return
	 */
	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
		return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
	}
	
	
	@PreDestroy
	public void closeDynamoDBClient() {
		dynamoDbClient.close();
	}
	
}

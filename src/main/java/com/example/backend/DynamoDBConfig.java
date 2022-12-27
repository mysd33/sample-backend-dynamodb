package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import com.example.fw.common.dynamodb.config.DynamoDBConfigPackage;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@ComponentScan(basePackageClasses = { DynamoDBConfigPackage.class })
public class DynamoDBConfig {

	@Bean
	public DynamoDBTableInitializer dynamoDBTableInitializer(DynamoDbClient dynamoDbClient,
			DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		return new SampleBackendDynamoDBTableInitializer(dynamoDbClient, dynamoDbEnhancedClient);
	}
}

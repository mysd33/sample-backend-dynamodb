package com.example.fw.common.dynamodb.config;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/// DynamoDBの共通の設定クラス
@Configuration
@RequiredArgsConstructor
public class DynamoDBCommonConfig {

    private final DynamoDbClient dynamoDbClient;

    /// DynamoDBEnhancedClient
    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }


    @PreDestroy
    void closeDynamoDBClient() {
        dynamoDbClient.close();
    }

}

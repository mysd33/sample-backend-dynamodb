package com.example.fw.common.dynamodb.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.CompositeDynamoDBTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBClientTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBEnhancedClientTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBTransactionManagerAspect;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * 
 * DynamoDBトランザクションの宣言的トランザクション管理機能の設定クラス
 *
 */
@Configuration
public class DynamoDBTransactionConfig {

    /**
     * DynamoDBTransactionManager
     * 
     */
    @Bean
    DynamoDBTransactionManager dynamoDBTransactionManager(DynamoDbEnhancedClient enhancedClient,
            DynamoDbClient dynamoDbClient) {
        return new CompositeDynamoDBTransactionManager(
                List.of(new DynamoDBEnhancedClientTransactionManager(enhancedClient),
                        new DynamoDBClientTransactionManager(dynamoDbClient)));
    }

    /**
     * DynamoDBTransactionManagerAspect
     * 
     */
    @Bean
    DynamoDBTransactionManagerAspect dynamoDBTransactionManagerAspect(
            DynamoDBTransactionManager transactionManager) {
        return new DynamoDBTransactionManagerAspect(transactionManager);
    }

}

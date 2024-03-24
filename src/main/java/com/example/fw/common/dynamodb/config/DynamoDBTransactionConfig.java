package com.example.fw.common.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.DynamoDBTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBTransactionManagerAspect;
import com.example.fw.common.dynamodb.EnhancedClientDynamoDBTransactionManager;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

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
     * @param enhancedClient
     * @return
     */
    @Bean
    public DynamoDBTransactionManager dynamoDBTransactionManager(DynamoDbEnhancedClient enhancedClient) {
        return new EnhancedClientDynamoDBTransactionManager(enhancedClient);
    }

    /**
     * DynamoDBTransactionManagerAspect
     * 
     * @param transactionManager
     * @return
     */
    @Bean
    public DynamoDBTransactionManagerAspect dynamoDBTransactionManagerAspect(
            DynamoDBTransactionManager transactionManager) {
        return new DynamoDBTransactionManagerAspect(transactionManager);
    }

}

package com.example.fw.common.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.DynamoDBEnhancedClientTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBTransactionManager;
import com.example.fw.common.dynamodb.DynamoDBTransactionManagerAspect;

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
        return new DynamoDBEnhancedClientTransactionManager(enhancedClient);
    }
    
    // DynamoDBClientTransactionManagerの場合のBean定義
    /*
    @Bean
    public DynamoDBTransactionManager dynamoDBTransactionManager(DynamoDbClient dynamoDbClient) {
        return new DynamoDBClientTransactionManager(dynamoDbClient);
    }*/


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

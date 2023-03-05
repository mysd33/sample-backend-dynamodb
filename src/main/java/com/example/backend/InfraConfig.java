package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import com.example.fw.common.dynamodb.config.DynamoDBConfigPackage;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = { DynamoDBConfigPackage.class })
public class InfraConfig {


    @Bean
    public DynamoDBTableInitializer dynamoDBTableInitializer(DynamoDbClient dynamoDbClient,
            DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return new SampleBackendDynamoDBTableInitializer(dynamoDbClient, dynamoDbEnhancedClient);
    }
    
    // DBアクセスしない場合のスタブ
    // @Bean
//    public TodoRepository todoRepositoryStub() {
//        return new TodoRepositoryStub();
//    }

}

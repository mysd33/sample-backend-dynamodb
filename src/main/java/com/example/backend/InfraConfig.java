package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import com.example.fw.common.dynamodb.config.DynamoDBConfigPackage;
import com.example.fw.common.logging.config.LoggingConfigPackage;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
// DynamoDBアクセスの設定、ロギング拡張設定を追加
@ComponentScan(basePackageClasses = { DynamoDBConfigPackage.class, LoggingConfigPackage.class })
public class InfraConfig {

    @Bean
    DynamoDBTableInitializer dynamoDBTableInitializer(DynamoDbClient dynamoDbClient,
            DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return new SampleBackendDynamoDBTableInitializer(dynamoDbClient, dynamoDbEnhancedClient);
    }

    // DBアクセスしない場合のスタブ
    // @Bean
//    TodoRepository todoRepositoryStub() {
//        return new TodoRepositoryStub();
//    }

}

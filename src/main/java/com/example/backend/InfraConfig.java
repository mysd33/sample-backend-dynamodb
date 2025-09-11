package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import com.example.fw.common.dynamodb.config.DynamoDBConfigPackage;
import com.example.fw.common.logging.config.LoggingExtensionConfig;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
@ComponentScan(basePackageClasses = { DynamoDBConfigPackage.class })
//Loggingの拡張設定を追加
@Import({ LoggingExtensionConfig.class })
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

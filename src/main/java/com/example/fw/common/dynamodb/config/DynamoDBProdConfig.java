package com.example.fw.common.dynamodb.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBProdInitializer;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB 本番用の設定クラス
 *
 */
@Configuration
@RequiredArgsConstructor
@Profile("production")
@EnableConfigurationProperties(DynamoDBConfigurationProperties.class)
public class DynamoDBProdConfig {
    private final DynamoDBConfigurationProperties dynamoDBConfigurationProperties;

    /**
     * DynamoDB 初期テーブル作成クラス
     */
    @Bean
    DynamoDBProdInitializer dynamoDBProdIntializer(DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBProdInitializer(dynamoDBTableInitializer);
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースなし）
     */
    @Profile("!xray")
    @Bean
    DynamoDbClient dynamoDbClientWithoutXRay() {
        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        return DynamoDbClient.builder()//
                .httpClientBuilder((ApacheHttpClient.builder()))//
                .region(region)//
                .build();
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースあり）
     */
    @Profile("xray")
    @Bean
    DynamoDbClient dynamoDbClientWithXRay() {
        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        return DynamoDbClient.builder()//
                .httpClientBuilder((ApacheHttpClient.builder()))//
                .region(region)
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build())
                .build();
    }

}

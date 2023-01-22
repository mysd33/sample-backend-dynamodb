package com.example.fw.common.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBProdInitializer;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB 本番用の設定クラス
 *
 */
@Configuration
@Profile("production")
public class DynamoDBProdConfig {
    @Value("${aws.dynamodb.region:ap-northeast-1}")
    private String regionName;

    /**
     * DynamoDB 初期テーブル作成クラス
     */
    @Bean
    public DynamoDBProdInitializer dynamoDBProdIntializer(DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBProdInitializer(dynamoDBTableInitializer);
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースなし）
     */
    @Profile("!xray")
    @Bean
    public DynamoDbClient dynamoDbClientWithoutXRay() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder().region(region).build();
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースあり）
     */
    @Profile("xray")
    @Bean
    public DynamoDbClient dynamoDbClientWithXRay() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder().region(region)
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build())
                .build();
    }

}

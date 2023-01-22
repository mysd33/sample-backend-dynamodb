package com.example.fw.common.dynamodb.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBLocalExecutor;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDBLocal起動のの設定クラス（開発時のみ）
 *
 */
@Configuration
@Profile("dev")
@ConditionalOnClass(DynamoDBProxyServer.class)
public class DynamoDBLocalConfig {
    @Value("${aws.dynamodb.region:ap-northeast-1}")
    private String regionName;
    @Value("${aws.dynamodb.port:8000}")
    private String port;

    /**
     * DynamoDB Local起動クラス
     */
    @Bean
    public DynamoDBLocalExecutor dynamoDBLocalExecutor(DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBLocalExecutor(port, dynamoDBTableInitializer);
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースなし）
     */
    @Profile("!xray")
    @Bean
    public DynamoDbClient dynamoDbClientWithoutXRay() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder().region(region).endpointOverride(URI.create("http://localhost:" + port)).build();
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースあり）
     */
    @Profile("xray")
    @Bean
    public DynamoDbClient dynamoDbClientWithXRay() {
        Region region = Region.of(regionName);
        return DynamoDbClient.builder().region(region).endpointOverride(URI.create("http://localhost:" + port))
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build())
                .build();
    }

}

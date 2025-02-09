package com.example.fw.common.dynamodb.config;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

//import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBLocalExecutor;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDBLocal起動のの設定クラス（開発時のみ）
 *
 */
@Configuration
@RequiredArgsConstructor
@Profile("dev")
@EnableConfigurationProperties(DynamoDBConfigurationProperties.class)
public class DynamoDBLocalConfig {
    private static final String HTTP_LOCALHOST = "http://localhost:";
    private static final String DUMMY = "dummy";    
    private final DynamoDBConfigurationProperties dynamoDBConfigurationProperties;

    /**
     * DynamoDB Local起動クラス
     */
    @Bean
    DynamoDBLocalExecutor dynamoDBLocalExecutor(DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBLocalExecutor(dynamoDBConfigurationProperties.getDynamodblocal().getPort(),
                dynamoDBTableInitializer);
    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースなし）
     */
    @Profile("!xray")
    @Bean
    DynamoDbClient dynamoDbClientWithoutXRay() {
        // ダミーのクレデンシャル
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(DUMMY, DUMMY);
        // @formatter:off        
        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        return DynamoDbClient.builder()
                .httpClientBuilder((ApacheHttpClient.builder()))                
                .region(region)
                .endpointOverride(URI.create(HTTP_LOCALHOST + dynamoDBConfigurationProperties.getDynamodblocal().getPort()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        // @formatter:on

    }

    /**
     * DynamoDB Localに接続するDynamoDBClient（X-Rayトレースあり）
     */
    @Profile("xray")
    @Bean
    DynamoDbClient dynamoDbClientWithXRay() {
        // ダミーのクレデンシャル

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(DUMMY, DUMMY);

        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        // @formatter:off    
        return DynamoDbClient.builder()
                .httpClientBuilder((ApacheHttpClient.builder()))
                .region(region)
                .endpointOverride(URI.create(HTTP_LOCALHOST + dynamoDBConfigurationProperties.getDynamodblocal().getPort()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder().addExecutionInterceptor(new TracingInterceptor()).build())
                .build();
        // @formatter:on        
    }

}

package com.example.fw.common.dynamodb.config;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBProdInitializer;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/// DynamoDB 本番用の設定クラス
@Configuration
@RequiredArgsConstructor
@Profile("production")
@EnableConfigurationProperties(DynamoDBConfigurationProperties.class)
public class DynamoDBProdConfig {

    private final DynamoDBConfigurationProperties dynamoDBConfigurationProperties;

    /// DynamoDB 初期テーブル作成クラス
    @Bean
    DynamoDBProdInitializer dynamoDBProdInitializer(
        DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBProdInitializer(dynamoDBTableInitializer);
    }

    /// DynamoDB Localに接続するDynamoDBClient
    @Profile("!xray")
    @Bean
    DynamoDbClient dynamoDbClient() {
        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        return DynamoDbClient.builder()
            //　標準リトライ戦略
            .overrideConfiguration(o -> o.retryStrategy(RetryMode.STANDARD))
            .httpClientBuilder(ApacheHttpClient.builder()
                .maxConnections(dynamoDBConfigurationProperties.getMaxConnections())
                .connectionTimeout(
                    Duration.ofMillis(dynamoDBConfigurationProperties.getConnectionTimeout()))
            )
            .region(region)
            .build();
    }

    /// DynamoDB Localに接続するDynamoDBClient（X-Ray SDK）<br>
    ///
    /// @deprecated X-Ray SDKは2027 年 2 月 25 日にサポート終了となるため削除予定
    @Deprecated(forRemoval = true)
    @Profile("xray")
    @Bean
    DynamoDbClient dynamoDbClientWithXRay() {
        Region region = Region.of(dynamoDBConfigurationProperties.getRegion());
        return DynamoDbClient.builder()
            //　標準リトライ戦略
            .overrideConfiguration(o -> o.retryStrategy(RetryMode.STANDARD)
                // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
                .addExecutionInterceptor(new TracingInterceptor())
            )
            .httpClientBuilder(ApacheHttpClient.builder()
                .maxConnections(dynamoDBConfigurationProperties.getMaxConnections())
                .connectionTimeout(
                    Duration.ofMillis(dynamoDBConfigurationProperties.getConnectionTimeout()))
            )
            .region(region)
            // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
            .overrideConfiguration(
                ClientOverrideConfiguration.builder()
                    .addExecutionInterceptor(new TracingInterceptor()).build())
            .build();
    }

}

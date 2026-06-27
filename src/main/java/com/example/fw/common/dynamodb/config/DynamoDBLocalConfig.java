package com.example.fw.common.dynamodb.config;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import com.example.fw.common.dynamodb.DynamoDBLocalExecutor;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/// DynamoDBLocal起動のの設定クラス（開発時のみ）
@Configuration
@RequiredArgsConstructor
@Profile("dev")
@EnableConfigurationProperties(DynamoDBConfigurationProperties.class)
public class DynamoDBLocalConfig {

    private static final String HTTP_LOCALHOST = "http://localhost:";
    private static final String DUMMY = "dummy";
    private final DynamoDBConfigurationProperties dynamoDBConfigurationProperties;

    /// DynamoDB Local起動クラス
    @Bean
    DynamoDBLocalExecutor dynamoDBLocalExecutor(DynamoDBTableInitializer dynamoDBTableInitializer) {
        return new DynamoDBLocalExecutor(
            dynamoDBConfigurationProperties.getDynamodblocal().getPort(),
            dynamoDBTableInitializer);
    }

    /// DynamoDB Localに接続するDynamoDBClient
    @Profile("!xray")
    @Bean
    DynamoDbClient dynamoDbClient() {
        // ダミーのクレデンシャル
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(DUMMY, DUMMY);

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
            .endpointOverride(URI.create(
                HTTP_LOCALHOST + dynamoDBConfigurationProperties.getDynamodblocal().getPort()))
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();

    }

    /// DynamoDB Localに接続するDynamoDBClient（X-Ray SDK）<br>
    ///
    /// @deprecated X-Ray SDKは2027 年 2 月 25 日にサポート終了となるため削除予定
    @Deprecated(forRemoval = true)
    @Profile("xray")
    @Bean
    DynamoDbClient dynamoDbClientWithXRay() {
        // ダミーのクレデンシャル

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(DUMMY, DUMMY);

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
            .endpointOverride(URI.create(
                HTTP_LOCALHOST + dynamoDBConfigurationProperties.getDynamodblocal().getPort()))
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            // 個別にDynamoDBへのAWS SDKの呼び出しをトレーシングできるように設定
            .overrideConfiguration(
                ClientOverrideConfiguration.builder()
                    .addExecutionInterceptor(new TracingInterceptor()).build())
            .build();

    }

}

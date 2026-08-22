package com.example.fw.common.dynamodb.config;

import java.time.Duration;
import software.amazon.awssdk.awscore.retry.AwsRetryStrategy;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.retries.api.BackoffStrategy;

/// DynamoDBのリトライポリシーを提供するユーティリティクラス
final class DynamoDBRetryPolicy {

    private DynamoDBRetryPolicy() {
    }

    // DynamoDBの標準リトライ戦略のみ、リトライ回数等の設定が通常と異なるため、これに合わせる値を定数を定義
    // [DynamoDbRetryPolicyクラス](https://github.com/aws/aws-sdk-java-v2/blob/master/services/dynamodb/src/main/java/software/amazon/awssdk/services/dynamodb/DynamoDbRetryPolicy.java)クラスと値合わせるため
    /// 最大リトライ回数（8回）
    private static final int MAX_ERROR_RETRY = 8;
    /// 最大再試行回数（9回）
    private static final int MAX_ATTEMPTS = MAX_ERROR_RETRY + 1;
    /// スロットリング以外のバックオフ戦略での基本遅延時間（25ミリ秒）
    private static final Duration BASE_DELAY = Duration.ofMillis(25);

    /// DynamoDBの標準リトライ戦略を返却する
    ///
    /// -Dオプションや環境変数で設定するとDynamoDBのみ標準リトライ戦略時の最大試行回数やバックオフ戦略の設定が異なるが、
    /// Builderで単純に標準リトライ戦略を指定すると通常のリトライ戦略になってしまうため、
    /// AWS SDKのDynamoDbRetryPolicyクラスの値と同じDynamoDB用のリトライ戦略を明示的に作成する。
    public static StandardRetryStrategy dynamoDBStandardRetryStrategy() {
        BackoffStrategy backoffStrategy = BackoffStrategy.exponentialDelay(
            DynamoDBRetryPolicy.BASE_DELAY,
            SdkDefaultRetrySetting.MAX_BACKOFF);
        return AwsRetryStrategy.standardRetryStrategy()
            .toBuilder()
            .maxAttempts(DynamoDBRetryPolicy.MAX_ATTEMPTS) //
            .backoffStrategy(backoffStrategy)
            .build();
    }
}

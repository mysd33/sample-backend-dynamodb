package com.example.fw.common.dynamodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.example.fw.common.constants.FrameworkConstants;

import lombok.Data;

/**
 * 
 * DynamoDBのプロパティクラス
 *
 */
@Data
@ConfigurationProperties(prefix = DynamoDBConfigurationProperties.PROPERTY)
public class DynamoDBConfigurationProperties {
    // TODO: プロパティ名の見直しを予定
    // DynamoDBの設定を保持するプロパティのプレフィックス
    static final String PROPERTY = FrameworkConstants.PROPERTY_BASE_NAME + "aws.dynamodb";
    // リージョン（デフォルト: ap-northeast-1）
    private String region = "ap-northeast-1";
    // ローカルDynamoDBの設定
    private DynamoDBLocalProperties dynamodblocal;

    @Data
    public static class DynamoDBLocalProperties {
        private int port = 8000;
    }
}

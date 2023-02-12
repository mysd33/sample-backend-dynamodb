package com.example.fw.common.dynamodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 
 * DynamoDBのプロパティクラス
 *
 */
@Data
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDBConfigurationProperties {
    private String region = "ap-northeast-1";
    private DynamoDBLocalProperties dynamodblocal;
    

    @Data
    public static class DynamoDBLocalProperties {
        private int port = 8000;
    }    
}

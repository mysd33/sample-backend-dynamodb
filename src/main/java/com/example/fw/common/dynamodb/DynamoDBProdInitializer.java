package com.example.fw.common.dynamodb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

/**
 * DynamoDB 本番向けにAP起動時初回テーブル作成するクラス
 */
@RequiredArgsConstructor
public class DynamoDBProdInitializer {
    private final DynamoDBTableInitializer dynamoDBTableInitializer;

    @Value("${aws.dynamodb.todo-tablename:Todo}")
    private String todoTableName;

    /**
     * DynamoDB Table作成
     * 
     * @throws Exception
     */
    @PostConstruct
    public void startup() {
        dynamoDBTableInitializer.createTables();
    }

}

package com.example.fw.common.dynamodb;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * DynamoDB 本番向けにAP起動時初回テーブル作成するクラス
 */
@RequiredArgsConstructor
public class DynamoDBProdInitializer {
    private final DynamoDBTableInitializer dynamoDBTableInitializer;

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

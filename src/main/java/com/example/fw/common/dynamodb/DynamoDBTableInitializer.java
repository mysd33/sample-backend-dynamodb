package com.example.fw.common.dynamodb;

/// DynamoDBの初回テーブル作成インタフェース
public interface DynamoDBTableInitializer {
    /// テーブルを作成する
    void createTables();
}

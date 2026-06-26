package com.example.backend.infra.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/// DynamoDBのTodoテーブルItemクラス
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TodoTableItem {
    public static final String TODO_USER_ID_INDEX = "todoUserIdIndex";
    // ID（パーティションキー）
    private String todoId;
    // ユーザーID（GSIパーティションキー）
    private String userId;
    // タイトル
    private String todoTitle;
    // 完了したかどうか
    private boolean finished;

    // 作成日時
    private String createdAt;
    // TODO: Instant型に変更
    // private Instant createAt;

    @DynamoDbPartitionKey
    public String getTodoId() {
        return todoId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = TODO_USER_ID_INDEX)
    public String getUserId() {
        return userId;
    }

}

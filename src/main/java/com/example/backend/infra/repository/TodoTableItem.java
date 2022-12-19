package com.example.backend.infra.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * DynamoDBのTodoテーブルItemクラス
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TodoTableItem {	
	// ID
	private String todoId;
	// タイトル
	private String todoTitle;
	// 完了したかどうか
	private boolean finished;
		
	// 作成日時
	private String createdAt;
	//TODO: Instant型に変更
	//private Instant createAt;

	@DynamoDbPartitionKey
	public String getTodoId() {
		return todoId;
	}

}
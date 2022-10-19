package com.example.backend.infra.repository;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * DynamoDBのTodoテーブルクラス
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TodoTable implements Serializable {
	private static final long serialVersionUID = -8221174350955399012L;
	//ID
	private String todoId;
	//タイトル
	private String todoTitle;
	//完了したかどうか
	private boolean finished;
	//作成日時
	private String createdAt;
	
	@DynamoDbPartitionKey
	public String getTodoId() {
		return todoId;
	}
	
	
	
	
	
}
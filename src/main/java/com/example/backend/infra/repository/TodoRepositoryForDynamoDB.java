package com.example.backend.infra.repository;

import java.util.Collection;
import java.util.Optional;

import com.example.backend.domain.model.Todo;
import com.example.backend.domain.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

/**
 * TodoRepositoryのDynamoDBアクセス実装
 */
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDB implements TodoRepository{
	private final DynamoDbEnhancedClient enhancedClient;
	
	//DynamoDbEnhancedClientでアクセス
    //https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb
	
	@Override
	public Optional<Todo> findById(String todoId) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Collection<Todo> findAll() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void create(Todo todo) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public boolean update(Todo todo) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void delete(Todo todo) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public long countByFinished(boolean finished) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

}

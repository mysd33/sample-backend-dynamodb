package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend.domain.repository.TodoRepository;
import com.example.backend.infra.repository.TodoRepositoryForDynamoDB;
import com.example.backend.infra.repository.TodoRepositoryStub;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

/**
 * 
 * インフラストラクチャ層の設定クラス
 *
 */
@Configuration
public class InfraConfig {

	//DBアクセスしない場合のスタブ	
	//@Bean
	public TodoRepository todoRepositoryStub() {
		return new TodoRepositoryStub();
	}


}

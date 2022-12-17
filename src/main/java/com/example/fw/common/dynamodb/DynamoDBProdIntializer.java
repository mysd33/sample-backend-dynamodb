package com.example.fw.common.dynamodb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

/**
 * DynamoDB 本番向けにAP起動時初回テーブル作成するクラス 
 */
@RequiredArgsConstructor
public class DynamoDBProdIntializer {
	private final DynamoDBTableInitializer dynamoDBTableInitializer;
			
	@Value("${aws.dynamodb.todo-tablename:Todo}")
	private String todoTableName; 
	
	/**
	 * DynamoDB Table作成
	 * @throws Exception
	 */
	@PostConstruct
	public void startup() throws Exception {
		// AP起動時動作確認用にテーブル作成
		dynamoDBTableInitializer.createTables();
	}

}

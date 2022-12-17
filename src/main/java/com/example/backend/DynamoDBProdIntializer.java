package com.example.backend;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.example.backend.domain.message.MessageIds;
import com.example.backend.infra.repository.TodoTableItem;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB 本番向けにAP起動時初回テーブル作成するクラス 
 */
@Slf4j
@RequiredArgsConstructor
public class DynamoDBProdIntializer {
	private final DynamoDbClient client;
	private final DynamoDbEnhancedClient enhancedClient;
	private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
				
	@Value("${aws.dynamodb.todo-tablename:Todo}")
	private String todoTableName; 
	
	/**
	 * DynamoDB Table作成
	 * @throws Exception
	 */
	@PostConstruct
	public void startup() throws Exception {
		//Todoテーブルがないことを確認
		if (client.listTables().tableNames().contains(todoTableName)) {
			appLogger.info(MessageIds.I_EX_0001, todoTableName);
			return;
		}
		//（参考）テーブル作成の実装例
	    //https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/examples-dynamodb-enhanced.html#dynamodb-enhanced-mapper-beantable
	    //https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/EnhancedCreateTable.java

		//Todoテーブル作成
	    DynamoDbTable<TodoTableItem> todoTable = enhancedClient.table(todoTableName, TableSchema.fromBean(TodoTableItem.class));
	    todoTable.createTable();
	    appLogger.info(MessageIds.I_EX_0002, todoTableName);	    
	}

}

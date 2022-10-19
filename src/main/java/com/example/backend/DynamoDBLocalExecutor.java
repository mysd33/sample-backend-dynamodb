package com.example.backend;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.example.backend.infra.repository.TodoTable;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * DynamoDB Localを起動するクラス 
 */
@RequiredArgsConstructor
public class DynamoDBLocalExecutor {
	private final DynamoDbEnhancedClient enhancedClient;
	private final String port;	
	
	private DynamoDBProxyServer server = null;		
	
	/**
	 * DynamoDB Local 起動
	 * @throws Exception
	 */
	@PostConstruct
	public void startup() throws Exception {
		//特定のフォルダに出力したsqlite4java-win32-x64.dllのパスを通す
	    System.setProperty("sqlite4java.library.path", "native-libs");
	    //DynamoDB Local起動
		final String[] localArgs = { "-inMemory" , "-port", port};
		server = ServerRunner.createServerFromCommandLineArgs(localArgs);
	    server.start();

		//AP起動時動作確認用にTodoテーブル作成
	    //https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/EnhancedCreateTable.java
	    DynamoDbTable<TodoTable> todoTable = enhancedClient.table("Todo", TableSchema.fromBean(TodoTable.class));
	    todoTable.createTable();
	    	    
	}

	/**
	 * DynamoDB Local終了
	 * @throws Exception
	 */
	@PreDestroy
	public void shutdown() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

}

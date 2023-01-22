package com.example.backend;

import org.springframework.beans.factory.annotation.Value;

import com.example.backend.domain.message.MessageIds;
import com.example.backend.infra.repository.TodoTableItem;
import com.example.fw.common.dynamodb.DynamoDBTableInitializer;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * 
 * DynnamoDBの初回テーブル作成実装クラス
 *
 */
@Slf4j
@RequiredArgsConstructor
public class SampleBackendDynamoDBTableInitializer implements DynamoDBTableInitializer {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final DynamoDbClient client;
    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.todo-tablename:Todo}")
    private String todoTableName;

    @Override
    public void createTables() {
        // Todoテーブルがないことを確認
        if (client.listTables().tableNames().contains(todoTableName)) {
            appLogger.info(MessageIds.I_EX_0001, todoTableName);
            return;
        }

        // AP起動時動作確認用にTodoテーブル作成
        // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/examples-dynamodb-enhanced.html#dynamodb-enhanced-mapper-beantable
        // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/EnhancedCreateTable.java
        DynamoDbTable<TodoTableItem> todoTable = enhancedClient.table(todoTableName,
                TableSchema.fromBean(TodoTableItem.class));
        todoTable.createTable();
        appLogger.info(MessageIds.I_EX_0002, todoTableName);
    }
}

package com.example.backend.infra.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.backend.domain.model.Todo;
import com.example.backend.domain.repository.TodoRepository;
import com.example.fw.common.dynamodb.DynamoDBEnhancedClientTransactionManager;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * TodoRepositoryのDynamoDB（トランザクション管理利用版）アクセス実装
 */
@XRayEnabled
//@Repository
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDBTransaction implements TodoRepository {
    // （参考）DynamoDbEnhancedClientの実装例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/ddb-en-client-use.html#ddb-en-client-use-basic-ops
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb

    private final DynamoDbEnhancedClient enhancedClient;
    private final TodoTableItemMapper todoTableItemMapper;

    @Value("${aws.dynamodb.todo-tablename:Todo}")
    private String todoTableName;
    private DynamoDbTable<TodoTableItem> todoTable;
    
    @PostConstruct
    private void init() {
        todoTable = createTodoTable(); 
    }   

    @Override
    public Optional<Todo> findById(String todoId) {
        Key key = Key.builder().partitionValue(todoId).build();
        TodoTableItem todoItem = todoTable.getItem(r -> r.key(key));
        Todo result = todoTableItemMapper.tableItemToModel(todoItem);
        return Optional.ofNullable(result);
    }

    @Override
    public Collection<Todo> findAll() {
        Iterable<TodoTableItem> items = todoTable.scan().items();
        return todoTableItemMapper.tableItemsToModels(items);
    }
   
    @Override
    public void create(Todo todo) {
        DynamoDbTable<TodoTableItem> todoDbTable = createTodoTable();
        TodoTableItem todoItem = todoTableItemMapper.modelToTableItem(todo);
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.getTransaction()
            .addPutItem(todoDbTable, todoItem);        
    }
   
    @Override
    public boolean update(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        TodoTableItem todoItem = todoTable.getItem(r -> r.key(key));
        todoItem.setTodoTitle(todo.getTodoTitle());
        todoItem.setFinished(todo.isFinished());        
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.getTransaction()
            .addUpdateItem(todoTable, todoItem);
        return true;
    }

    @Override
    public void delete(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        todoTable.deleteItem(key);
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.getTransaction()
            .addDeleteItem(todoTable, key);
    }
    
    @Override
    public long countByFinished(boolean finished) {
        AttributeValue att = AttributeValue.builder().bool(finished).build();
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":value", att);
        Expression expression = Expression.builder().expression("finished = :value").expressionValues(expressionValues)
                .build();
        return todoTable.scan(r -> r.filterExpression(expression)).items().stream().count();
    }

    private DynamoDbTable<TodoTableItem> createTodoTable() {
        return enhancedClient.table(todoTableName,
                TableSchema.fromBean(TodoTableItem.class));        
    }

}

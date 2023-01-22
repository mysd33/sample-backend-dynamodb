package com.example.backend.infra.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.backend.domain.model.Todo;
import com.example.backend.domain.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * TodoRepositoryのDynamoDBアクセス実装
 */
@XRayEnabled
@Repository
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDB implements TodoRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.todo-tablename:Todo}")
    private String todoTableName;

    // （参考）DynamoDbEnhancedClientの実装例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/examples-dynamodb-enhanced.html
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb

    @Override
    public Optional<Todo> findById(String todoId) {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        Key key = Key.builder().partitionValue(todoId).build();
        TodoTableItem todoItem = dynamoDb.getItem(r -> r.key(key));
        Todo result = TodoTableItemMapper.INSTANCE.tableItemToModel(todoItem);
        return Optional.ofNullable(result);
    }

    @Override
    public Collection<Todo> findAll() {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        Iterable<TodoTableItem> items = dynamoDb.scan().items();
        List<Todo> todoList = new ArrayList<>();
        items.forEach(item -> 
            todoList.add(TodoTableItemMapper.INSTANCE.tableItemToModel(item))
        );
        return todoList;
    }

    @Override
    public void create(Todo todo) {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        TodoTableItem todoItem = TodoTableItemMapper.INSTANCE.modelToTableItem(todo);
        dynamoDb.putItem(todoItem);
    }

    @Override
    public boolean update(Todo todo) {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        TodoTableItem todoItem = dynamoDb.getItem(r -> r.key(key));
        todoItem.setTodoTitle(todo.getTodoTitle());
        todoItem.setFinished(todo.isFinished());
        dynamoDb.updateItem(todoItem);
        return false;
    }

    @Override
    public void delete(Todo todo) {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        dynamoDb.deleteItem(key);
    }

    @Override
    public long countByFinished(boolean finished) {
        DynamoDbTable<TodoTableItem> dynamoDb = createDynamoDBClient();
        AttributeValue att = AttributeValue.builder().bool(finished).build();
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":value", att);
        Expression expression = Expression.builder().expression("finished = :value").expressionValues(expressionValues)
                .build();
        return dynamoDb.scan(r -> r.filterExpression(expression)).items().stream().count();
    }

    private DynamoDbTable<TodoTableItem> createDynamoDBClient() {
        return enhancedClient.table(todoTableName,
                TableSchema.fromBean(TodoTableItem.class));        
    }

}

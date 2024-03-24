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
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

/**
 * TodoRepositoryのDynamoDBアクセス実装
 */
@Slf4j
@XRayEnabled
@Repository
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDB implements TodoRepository {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

    // （参考）DynamoDbEnhancedClientの実装例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/ddb-en-client-use.html#ddb-en-client-use-basic-ops
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/enhanced
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
        TodoTableItem todoItem = todoTableItemMapper.modelToTableItem(todo);
        if (appLogger.isDebugEnabled()) {
            PutItemEnhancedResponse<TodoTableItem> response = todoTable.putItemWithResponse(
                    r -> r.item(todoItem).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL).build());
            appLogger.debug("消費キャパシティユニット:{}", response.consumedCapacity().capacityUnits());

        } else {
            todoTable.putItem(todoItem);
        }
    }

    @Override
    public boolean update(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        TodoTableItem todoItem = todoTable.getItem(r -> r.key(key));
        todoItem.setTodoTitle(todo.getTodoTitle());
        todoItem.setFinished(todo.isFinished());
        if (appLogger.isDebugEnabled()) {
            UpdateItemEnhancedResponse<TodoTableItem> response = todoTable.updateItemWithResponse(
                    r -> r.item(todoItem).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL).build());
            appLogger.debug("消費キャパシティユニット:{}", response.consumedCapacity().capacityUnits());
        } else {
            todoTable.updateItem(todoItem);
        }
        return true;
    }

    @Override
    public void delete(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        if (appLogger.isDebugEnabled()) {
            DeleteItemEnhancedResponse<TodoTableItem> response = todoTable.deleteItemWithResponse(
                    r -> r.key(key).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL).build());
            appLogger.debug("消費キャパシティユニット:{}", response.consumedCapacity().capacityUnits());
        } else {
            todoTable.deleteItem(key);
        }
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
        return enhancedClient.table(todoTableName, TableSchema.fromBean(TodoTableItem.class));
    }

}

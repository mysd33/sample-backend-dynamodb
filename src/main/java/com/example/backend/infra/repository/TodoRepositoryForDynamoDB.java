package com.example.backend.infra.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
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
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

/// TodoRepositoryのDynamoDBアクセス実装
@Slf4j
@XRayEnabled
// @Repository
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDB implements TodoRepository {
    private static final String CONSUMED_CAPACITY_DEBUG_MESSAGE = "消費キャパシティユニット:{}";

    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);

    // （参考）DynamoDbEnhancedClientの実装例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/ddb-en-client-use.html#ddb-en-client-use-basic-ops
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/enhanced
    private final DynamoDbEnhancedClient enhancedClient;
    private final TodoTableItemMapper todoTableItemMapper;

    @Value("${example.dynamodb.todo-tablename:Todo}")
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
    public Collection<Todo> findAllByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return todoTable.scan().items().stream().map(todoTableItemMapper::tableItemToModel)
                    .toList();
        }
        var items = todoTable.index(TodoTableItem.TODO_USER_ID_INDEX).query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build())));
        return todoTableItemMapper.tableItemsToModels(items);
    }

    @Override
    public void create(Todo todo) {
        TodoTableItem todoItem = todoTableItemMapper.modelToTableItem(todo);
        if (appLogger.isDebugEnabled()) {
            var response = todoTable.putItemWithResponse(
                    r -> r.item(todoItem).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL));
            appLogger.debug(CONSUMED_CAPACITY_DEBUG_MESSAGE,
                    response.consumedCapacity().capacityUnits());

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
            var response = todoTable.updateItemWithResponse(
                    r -> r.item(todoItem).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL));
            appLogger.debug(CONSUMED_CAPACITY_DEBUG_MESSAGE,
                    response.consumedCapacity().capacityUnits());
        } else {
            todoTable.updateItem(todoItem);
        }
        return true;
    }

    @Override
    public boolean delete(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).build();
        if (appLogger.isDebugEnabled()) {
            var response = todoTable.deleteItemWithResponse(
                    r -> r.key(key).returnConsumedCapacity(ReturnConsumedCapacity.TOTAL));
            appLogger.debug(CONSUMED_CAPACITY_DEBUG_MESSAGE,
                    response.consumedCapacity().capacityUnits());
        } else {
            todoTable.deleteItem(key);
        }
        return true;
    }

    @Override
    public long countByFinished(String userId, boolean finished) {
        AttributeValue att = AttributeValue.builder().bool(finished).build();
        var expressionValues = new HashMap<String, AttributeValue>();
        expressionValues.put(":value", att);
        Expression expression = Expression.builder().expression("finished = :value")
                .expressionValues(expressionValues).build();
        var items = todoTable.index(TodoTableItem.TODO_USER_ID_INDEX)
                .query(r -> r
                        .queryConditional(QueryConditional
                                .keyEqualTo(Key.builder().partitionValue(userId).build()))
                        .filterExpression(expression));
        return todoTableItemMapper.tableItemsToModels(items).size();
    }

    private DynamoDbTable<TodoTableItem> createTodoTable() {
        return enhancedClient.table(todoTableName, TableSchema.fromBean(TodoTableItem.class));
    }

}

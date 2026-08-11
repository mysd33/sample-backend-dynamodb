package com.example.backend.infra.repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.backend.domain.model.Todo;
import com.example.backend.domain.repository.TodoRepository;
import com.example.fw.common.dynamodb.DynamoDBEnhancedClientTransactionManager;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/// TodoRepositoryのDynamoDB（トランザクション管理利用版）アクセス実装
@XRayEnabled
@Repository
@RequiredArgsConstructor
public class TodoRepositoryForDynamoDBTransaction implements TodoRepository {
    // （参考）DynamoDbEnhancedClientの実装例
    // https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/ddb-en-client-use.html#ddb-en-client-use-basic-ops
    // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb

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
    public Optional<Todo> findOne(String todoId) {
        var todoItems = todoTable.query(r -> r.queryConditional(
            QueryConditional.keyEqualTo(Key.builder().partitionValue(todoId).build())));
        var todoItem = todoItems.items().stream().findFirst().orElse(null);
        var todo = todoTableItemMapper.tableItemToModel(todoItem);
        return Optional.ofNullable(todo);
    }

    @Override
    public Optional<Todo> findOneByUserId(String todoId, String userId) {
        var key = Key.builder().partitionValue(todoId).sortValue(userId).build();
        var todoItem = todoTable.getItem(r -> r.key(key));
        var todo = todoTableItemMapper.tableItemToModel(todoItem);
        return todo != null ? Optional.of(todo) : Optional.empty();
    }

    @Override
    public Collection<Todo> findAllByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return todoTable.scan().items().stream().map(todoTableItemMapper::tableItemToModel)
                .toList();
        }
        var todoItems = todoTable.index(TodoTableItem.TODO_USER_ID_INDEX)
            .query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build())));
        return todoTableItemMapper.tableItemsToModels(todoItems);
    }

    @Override
    public long countByFinishedStatus(String userId, boolean finished) {
        var att = AttributeValue.builder().bool(finished).build();
        var expressionValues = new HashMap<String, AttributeValue>();
        expressionValues.put(":value", att);
        var expression = Expression.builder().expression("finished = :value")
            .expressionValues(expressionValues).build();
        var items = todoTable.index(TodoTableItem.TODO_USER_ID_INDEX)
            .query(r -> r
                .queryConditional(QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(userId).build()))
                .filterExpression(expression));
        return todoTableItemMapper.tableItemsToModels(items).size();
    }


    @Override
    public void create(Todo todo) {
        var todoItem = todoTableItemMapper.modelToTableItem(todo);
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.addPutItem(todoTable, todoItem);
    }

    @Override
    public boolean update(Todo todo) {
        var todoItem = todoTableItemMapper.modelToTableItem(todo);
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.addUpdateItem(todoTable, todoItem);
        return true;
    }

    @Override
    public boolean updateFinishedById(String todoId, String userId) {
        Key key = Key.builder().partitionValue(todoId).sortValue(userId).build();
        TodoTableItem todoItem = todoTable.getItem(r -> r.key(key));
        if (todoItem != null && todoItem.getUserId().equals(userId)) {
            todoItem.setFinished(true);
            // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
            // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
            DynamoDBEnhancedClientTransactionManager.addUpdateItem(todoTable, todoItem);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Todo todo) {
        Key key = Key.builder().partitionValue(todo.getTodoId()).sortValue(todo.getUserId())
            .build();
        todoTable.deleteItem(key);
        // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
        // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
        DynamoDBEnhancedClientTransactionManager.addDeleteItem(todoTable, key);
        return true;
    }

    @Override
    public boolean deleteById(String todoId, String userId) {
        Key key = Key.builder().partitionValue(todoId).sortValue(userId).build();
        TodoTableItem todoItem = todoTable.getItem(r -> r.key(key));
        if (todoItem != null && todoItem.getUserId().equals(userId)) {
            // DynamoDBTransactionManagerを使ってDynamoDBTransactionに登録、この時点ではDynamoDBにアクセスしない
            // Serviceのメソッドに@DynamoDBTransactional付与することでトランザクション境界に設定され、メソッド終了時にコミットする。
            DynamoDBEnhancedClientTransactionManager.addDeleteItem(todoTable, key);
            return true;
        }
        return false;
    }

    private DynamoDbTable<TodoTableItem> createTodoTable() {
        return enhancedClient.table(todoTableName, TableSchema.fromBean(TodoTableItem.class));
    }

}

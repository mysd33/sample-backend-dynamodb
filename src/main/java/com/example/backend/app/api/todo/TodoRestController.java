package com.example.backend.app.api.todo;

import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.backend.domain.message.MessageIds;
import com.example.backend.domain.model.Todo;
import com.example.backend.domain.service.todo.TodoService;
import com.example.fw.common.dynamodb.DynamoDBTransactionUtil;
import com.example.fw.common.exception.DynamoDBTransactionBusinessException;
import com.example.fw.common.exception.SystemException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

/**
 * 
 * Todoを扱うREST APIのRestControllerクラス
 *
 */
@Tag(name = "Todo", description = "Todo API")
@XRayEnabled
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoRestController {
    private final TodoService todoService;
    private final TodoMapper todoMapper;

    /**
     * Todoリストを取得する
     * 
     * @return Todoリスト
     */
    @Operation(summary = "Todoリスト取得", description = "Todoリストを取得する。")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoResource> getTodos() {
        Collection<Todo> todos = todoService.findAll();
        return todoMapper.modelsToResources(todos);
    }

    /**
     * 指定したTodo IDに対応するTodoを取得する
     * 
     * @param todoId Todo ID
     * @return
     */
    @Operation(summary = "Todo取得", description = "指定したTodo IDに対応するTodoを取得する。")
    @GetMapping("{todoId}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResource getTodo(@Parameter(description = "Todo ID") @PathVariable @UUID String todoId) {
        Todo todo = todoService.findOne(todoId);
        return todoMapper.modelToResource(todo);
    }

    /**
     * Todoを登録する
     * 
     * @param todoResource 登録するTodo
     * @return 登録したTodo
     */
    @Operation(summary = "Todo登録", description = "Todoを登録する。")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResource postTodos(
            @Parameter(description = "登録するTodo") @RequestBody @Validated TodoResource todoResource) {
        try {
            Todo createdTodo = todoService.create(todoMapper.resourceToModel(todoResource));
            return todoMapper.modelToResource(createdTodo);
        } catch (TransactionCanceledException e) {
            // 理由コードごとにハンドリングしたい場合は、以下のようにキャッチしてハンドリング
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailed(e)) {
                // 条件付き更新に失敗した場合に業務エラーとしてリスローする例
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5004, todoResource.getTodoTitle());
            } else if (DynamoDBTransactionUtil.isTransactionConflict(e)) {
                // トランザクションの競合が発生した場合の処理にシステムエラーにする例
                throw new SystemException(e, MessageIds.E_EX_9002);
            }
            // 2つの理由コードが混在するケースでも業務エラーにする配慮する場合はこちらを使用
            // DynamoDBトランザクションを利用する場合は、トランザクション失敗時の例外をハンドリング
            // 条件付き更新に失敗またはトランザクションが競合した場合は、業務エラーとしてリスローする例
            //@formatter:off
            /*
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailedOrConflict(e)) {
                
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5004, todoResource.getTodoTitle());
            }*/
            //@formatter:on
            throw e;
        }

    }

    /**
     * バッチ処理向けに登録件数をチェックせずにTodoを登録する
     * 
     * @param todoResource 登録するTodo
     * @return 登録したTodo
     */
    @Operation(summary = "バッチ処理用Todo登録", description = "バッチ処理向けに登録件数をチェックせずにTodoを登録する。")
    @PostMapping("batch")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResource postTodosForBatch(
            @Parameter(description = "登録するTodo") @RequestBody @Validated TodoResource todoResource) {
        try {
            Todo createdTodo = todoService.createForBatch(todoMapper.resourceToModel(todoResource));
            return todoMapper.modelToResource(createdTodo);
        } catch (TransactionCanceledException e) {
            // 理由コードごとにハンドリングしたい場合は、以下のようにキャッチしてハンドリング
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailed(e)) {
                // 条件付き更新に失敗した場合に業務エラーとしてリスローする例
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5004, todoResource.getTodoTitle());
            } else if (DynamoDBTransactionUtil.isTransactionConflict(e)) {
                // トランザクションの競合が発生した場合の処理にシステムエラーにする例
                throw new SystemException(e, MessageIds.E_EX_9002);
            }
            // 2つの理由コードが混在するケースでも業務エラーにする配慮する場合はこちらを使用
            // DynamoDBトランザクションを利用する場合は、トランザクション失敗時の例外をハンドリング
            // 条件付き更新に失敗またはトランザクションが競合した場合は、業務エラーとしてリスローする例
            //@formatter:off
            /*
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailedOrConflict(e)) {
                
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5004, todoResource.getTodoTitle());
            }*/
            //@formatter:on
            throw e;
        }
    }

    /**
     * 指定したTodo IDのTodoを完了状態に更新する
     * 
     * @param todoId Todo ID
     * @return 更新したTodo
     */
    @Operation(summary = "Todo完了", description = "指定したTodo IDのTodoを完了状態に更新する。")
    @PutMapping("{todoId}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResource putTodo(@Parameter(description = "Todo ID") @PathVariable @UUID String todoId) {
        try {
            Todo finishedTodo = todoService.finish(todoId);
            return todoMapper.modelToResource(finishedTodo);
        } catch (TransactionCanceledException e) {
            // 理由コードごとにハンドリングしたい場合は、以下のようにキャッチしてハンドリング
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailed(e)) {
                // 条件付き更新に失敗した場合に業務エラーとしてリスローする例
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5005, todoId);
            } else if (DynamoDBTransactionUtil.isTransactionConflict(e)) {
                // トランザクションの競合が発生した場合の処理にシステムエラーにする例
                throw new SystemException(e, MessageIds.E_EX_9002);
            }
            // 2つの理由コードが混在するケースでも業務エラーにする配慮する場合はこちらを使用
            // DynamoDBトランザクションを利用する場合は、トランザクション失敗時の例外をハンドリング
            // 条件付き更新に失敗またはトランザクションが競合した場合は、業務エラーとしてリスローする例
            //@formatter:off
            /*
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailedOrConflict(e)) {
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5005, todoId);
            }*/
            //@formatter:on
            throw e;
        }
    }

    /**
     * 指定したTodo IDのTodoを削除する。
     * 
     * @param todoId Todo ID
     */
    @Operation(summary = "Todo削除", description = "指定したTodo IDのTodoを削除する。")
    @DeleteMapping("{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@Parameter(description = "Todo ID") @PathVariable @UUID String todoId) {
        try {
            todoService.delete(todoId);
        } catch (TransactionCanceledException e) {
            // 理由コードごとにハンドリングしたい場合は、以下のようにキャッチしてハンドリング
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailed(e)) {
                // 条件付き更新に失敗した場合に業務エラーとしてリスローする例
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5006, todoId);
            } else if (DynamoDBTransactionUtil.isTransactionConflict(e)) {
                // トランザクションの競合が発生した場合の処理にシステムエラーにする例
                throw new SystemException(e, MessageIds.E_EX_9002);
            }
            // 2つの理由コードが混在するケースでも業務エラーにする配慮する場合はこちらを使用
            // DynamoDBトランザクションを利用する場合は、トランザクション失敗時の例外をハンドリング
            // 条件付き更新に失敗またはトランザクションが競合した場合は、業務エラーとしてリスローする例
            //@formatter:off
            /*
            if (DynamoDBTransactionUtil.isTransactionConditionalCheckFailedOrConflict(e)) {
                throw new DynamoDBTransactionBusinessException(e, MessageIds.W_EX_5006, todoId);
            }*/
            //@formatter:on
            throw e;
        }
    }
}

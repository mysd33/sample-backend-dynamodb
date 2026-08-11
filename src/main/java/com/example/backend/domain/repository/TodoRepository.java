package com.example.backend.domain.repository;

import com.example.backend.domain.model.Todo;
import java.util.Collection;
import java.util.Optional;

/// TodoのRepositoryインタフェース
public interface TodoRepository {

    /// Todoを取得する
    ///
    /// @param todoId TodoのID
    /// @return 取得したTodo
    Optional<Todo> findOne(String todoId);

    /// Todoを取得する
    ///
    /// @param todoId TodoのID
    /// @param userId ユーザID
    /// @return 取得したTodo
    Optional<Todo> findOneByUserId(String todoId, String userId);

    /// 指定したユーザのTodoを全件取得する。
    ///
    /// @param userId ユーザID
    /// @return Todoの全件リスト
    Collection<Todo> findAllByUserId(String userId);

    /// 指定した完了ステータスのTodoの件数を取得
    ///
    /// @param userId   ユーザID
    /// @param finished 完了ステータス
    /// @return 件数
    long countByFinishedStatus(String userId, boolean finished);

    /// Todoを作成する
    ///
    /// @param todo 作成するTodo
    void create(Todo todo);

    /// Todoを更新する
    ///
    /// @param todo 更新するTodo
    /// @return 更新成功したかどうか
    boolean update(Todo todo);

    /// Todoの完了状態を更新する
    ///
    /// @param todoId 完了するTodoのID
    /// @param userId 完了するTodoのユーザID
    /// @return 更新成功したかどうか
    boolean updateFinishedById(String todoId, String userId);

    /// Todoを削除する
    ///
    /// @param todo 削除するTodo
    /// @return 削除成功したかどうか
    boolean delete(Todo todo);

    /// Todoを削除する
    ///
    /// @param todoId 削除するTodoのID
    /// @param userId 削除するTodoのユーザID
    /// @return 削除成功したかどうか
    boolean deleteById(String todoId, String userId);


}

package com.example.backend.infra.repository;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import com.example.backend.domain.model.Todo;

/**
 * TodoとTodoTableのオブジェクトマッパークラス
 *
 */
@Mapper(componentModel = ComponentModel.SPRING)
public interface TodoTableItemMapper {

    /**
     * モデルからテーブルデータに変換
     */
    TodoTableItem modelToTableItem(Todo todo);

    /**
     * リソースからモデルに変換
     */
    Todo tableItemToModel(TodoTableItem todoItem);
    
    
    /**
     * リソースのリストからモデルのリストに変換
     */
    default List<Todo> tableItemsToModels(Iterable<TodoTableItem> todoItems) {
        List<Todo> todoList = new ArrayList<>();
        todoItems.forEach(item -> 
            todoList.add(tableItemToModel(item))
        );
        return todoList; 
    }

}

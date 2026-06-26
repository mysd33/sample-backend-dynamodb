package com.example.backend.infra.repository;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import com.example.backend.domain.model.Todo;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

/// TodoとTodoTableのオブジェクトマッパークラス
@Mapper(componentModel = ComponentModel.SPRING)
public interface TodoTableItemMapper {

    /// モデルからテーブルデータに変換
    TodoTableItem modelToTableItem(Todo todo);

    /// リソースからモデルに変換
    Todo tableItemToModel(TodoTableItem todoItem);


    /// リソースのリストからモデルのリストに変換
    default List<Todo> tableItemsToModels(Iterable<Page<TodoTableItem>> pages) {
        var todoList = new ArrayList<Todo>();
        for (Page<TodoTableItem> page : pages) {
            var todoItems = page.items();
            todoItems.forEach(item -> todoList.add(tableItemToModel(item)));
        }
        return todoList;
    }

}

package com.example.backend.infra.repository;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.backend.domain.model.Todo;

/**
 * TodoとTodoTableのオブジェクトマッパークラス 
 *
 */
@Mapper(componentModel = "spring")
public interface TodoTableItemMapper {
	/**
	 * マッパーインスタンス
	 */
	TodoTableItemMapper INSTANCE = Mappers.getMapper(TodoTableItemMapper.class);
	/**
	 * モデルからテーブルデータに変換
	 */
	TodoTableItem modelToTableItem(Todo todo);
	/**
	 * リソースからモデルに変換
	 */
	Todo tableItemToModel(TodoTableItem todoItem);

}

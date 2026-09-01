package com.example.backend.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/// Todoクラス
@Data
@Builder
public class Todo {

    // ID
    private String todoId;
    // Todoを所有するユーザID
    private String userId;
    // タイトル
    private String todoTitle;
    // 完了したかどうか
    private boolean finished;
    // 作成日時
    private LocalDateTime createdAt;
}

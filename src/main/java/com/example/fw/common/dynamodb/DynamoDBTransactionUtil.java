package com.example.fw.common.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.CancellationReason;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

/**
 * DynamoDBのトランザクションに関するユーティリティクラス
 *
 */
public final class DynamoDBTransactionUtil {
    private DynamoDBTransactionUtil() {
    }

    /**
     * エラーの原因がトランザクション実行中にConditionCheckに失敗
     * （TransactionCanceledExceptionが発生しConditionalCheckFailedのみが含まれている）かどうかを判定します
     * 
     * @param e TransactionCanceledException
     * @return トランザクション実行中にConditionCheckに失敗した場合はtrueを返す
     */
    public static boolean isTransactionConditionalCheckFailed(TransactionCanceledException e) {
        return containsOnlyTargetCancellationReason(e, "ConditionalCheckFailed");
    }

    /**
     * エラーの原因がトランザクション実行中にトランザクションの競合が発生
     * （TransactionCanceledExceptionが発生しTransactionConflictのみが含まれている）かどうかを判定します。
     * 
     * @param e TransactionCanceledException
     * @return トランザクション実行中にトランザクションの競合が発生し失敗した場合はtrueを返す。
     */
    public static boolean isTransactionConflict(TransactionCanceledException e) {
        return containsOnlyTargetCancellationReason(e, "TransactionConflict");
    }

    /**
     * TransactionCanceledExceptionに指定された原因コードのみが含まれているかを判定します
     * 
     * @param e                TransactionCanceledException
     * @param targetReasonCode 指定した原因コード
     * @return 指定された原因コードのみが含まれていればtrueを返す
     */
    private static boolean containsOnlyTargetCancellationReason(TransactionCanceledException e,
            String targetReasonCode) {
        // トランザクションのキャンセルの原因については以下のドキュメントを参照
        // https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/APIReference/API_TransactWriteItems.html
        boolean contains = false;
        for (CancellationReason reason : e.cancellationReasons()) {
            String reasonCode = reason.code();
            if (targetReasonCode.equals(reasonCode)) {
                contains = true;
            } else if (!"None".equals(reasonCode)) {
                return false;
            }
        }
        return contains;
    }

}

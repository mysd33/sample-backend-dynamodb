package com.example.fw.common.dynamodb;

import java.util.Set;

import software.amazon.awssdk.services.dynamodb.model.CancellationReason;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

/**
 * DynamoDBのトランザクションに関するユーティリティクラス
 *
 */
public final class DynamoDBTransactionUtil {
    private static final String NONE = "None";
    private static final String TRANSACTION_CONFLICT = "TransactionConflict";
    private static final String CONDITIONAL_CHECK_FAILED = "ConditionalCheckFailed";

    private DynamoDBTransactionUtil() {
    }

    /**
     * エラーの原因がトランザクション実行中にConditionCheckに失敗
     * （TransactionCanceledExceptionが発生しConditionalCheckFailedのみが含まれている）かどうかを判定します
     * 
     * @param e TransactionCanceledException
     * @return トランザクション実行中にConditionCheckに失敗した場合はtrueを返す
     */
    public static boolean isTransactionConditionalCheckFailed(final TransactionCanceledException e) {
        return containsOnlyTargetCancellationReason(e, CONDITIONAL_CHECK_FAILED);
    }

    /**
     * エラーの原因がトランザクション実行中にトランザクションの競合が発生
     * （TransactionCanceledExceptionが発生しTransactionConflictのみが含まれている）かどうかを判定します。
     * 
     * @param e TransactionCanceledException
     * @return トランザクション実行中にトランザクションの競合が発生し失敗した場合はtrueを返す。
     */
    public static boolean isTransactionConflict(final TransactionCanceledException e) {
        return containsOnlyTargetCancellationReason(e, TRANSACTION_CONFLICT);
    }

    /**
     * エラーの原因がトランザクション実行中にConditionCheckに失敗
     * （TransactionCanceledExceptionが発生しConditionalCheckFailedが含まれている）か、あるいは、
     * トランザクション実行中にトランザクションの競合が発生
     * （TransactionCanceledExceptionが発生しTransactionConflicが含まれている）かどうかを判定します。
     * 
     */
    public static boolean isTransactionConditionalCheckFailedOrConflict(final TransactionCanceledException e) {
        return containsTargetCancellationReasons(e, Set.of(CONDITIONAL_CHECK_FAILED, TRANSACTION_CONFLICT));
    }

    /**
     * TransactionCanceledExceptionに指定された原因コードのみが含まれているかを判定します
     * 
     * @param e                TransactionCanceledException
     * @param targetReasonCode 指定した原因コード
     * @return 指定された原因コードのみが含まれていればtrueを返す
     */
    private static boolean containsOnlyTargetCancellationReason(final TransactionCanceledException e,
            final String targetReasonCode) {
        return containsTargetCancellationReasons(e, Set.of(targetReasonCode));
    }

    /**
     * 
     * /** TransactionCanceledExceptionに指定された原因コードのいずれが含まれているかを判定します
     * 
     * @param e                 TransactionCanceledException
     * @param targetReasonCodes 指定した原因コード
     * @return 指定された原因コードのいずれかが含まれていればtrueを返す
     */
    private static boolean containsTargetCancellationReasons(final TransactionCanceledException e,
            final Set<String> targetReasonCodes) {
        if (e == null || targetReasonCodes == null || targetReasonCodes.isEmpty()) {
            return false;
        }
        // トランザクションのキャンセルの原因については以下のドキュメントを参照
        // https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/APIReference/API_TransactWriteItems.html
        boolean contains = false;
        for (CancellationReason reason : e.cancellationReasons()) {
            String reasonCode = reason.code();
            if (targetReasonCodes.contains(reasonCode)) {
                contains = true;
            } else if (!NONE.equals(reasonCode)) {
                return false;
            }
        }
        return contains;
    }

}

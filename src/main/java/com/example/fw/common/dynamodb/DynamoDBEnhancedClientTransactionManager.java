package com.example.fw.common.dynamodb;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;

/**
 * DynamoDbEnhancedClientを利用したDynamoDBTransactionManagerの実装クラスです。
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DynamoDBEnhancedClientTransactionManager implements DynamoDBTransactionManager {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    // トランザクションをスレッドローカルで管理
    private static final ThreadLocal<DynamoDBEnhancedClientTransaction> transactionStore = new ThreadLocal<>();
    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public void startTransaction() {
        appLogger.debug("トランザクション開始");
        transactionStore.set(new DynamoDBEnhancedClientTransaction());
    }

    @Override
    public void commit() {
        DynamoDBEnhancedClientTransaction tx = transactionStore.get();
        if (tx.hasTransactionItems()) {
            appLogger.debug("トランザクションコミット");
            enhancedClient.transactWriteItems(tx.getTransactWriteItemsEnhancedRequest());
        } else {
            appLogger.debug("トランザクションアイテムなし");
        }
    }

    @Override
    public void rollback() {
        appLogger.debug("トランザクションロールバック");
        // 何もしない
    }

    @Override
    public void close() throws Exception {
        endTransaction();
    }

    private void endTransaction() {
        appLogger.debug("トランザクション終了");
        transactionStore.remove();
    }

    /**
     * トランザクションオブジェクトを返却する
     * 
     * @return トランザクションオブジェクト
     */
    public static DynamoDBEnhancedClientTransaction getTransaction() {
        return transactionStore.get();
    }

    /**
     * ConditionCheckの操作を現在のトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param conditionCheck      ConditionCheck
     * @return 現在のトランザクションEnhancedClientDynamoDBTransaction
     */
    public static <T> DynamoDBEnhancedClientTransaction addConditionCheck(
            final MappedTableResource<T> mappedTableResource, final ConditionCheck<T> conditionCheck) {
        return getTransaction().addConditionCheck(mappedTableResource, conditionCheck);
    }

    /**
     * PutItemの操作を現在のトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param item                登録するテーブルのアイテム
     * @return 現在のトランザクションEnhancedClientDynamoDBTransaction
     */
    public static <T> DynamoDBEnhancedClientTransaction addPutItem(final MappedTableResource<T> mappedTableResource,
            final T item) {
        return getTransaction().addPutItem(mappedTableResource, item);
    }

    /**
     * UpdateItemの操作を現在のトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param item                更新するテーブルのアイテム
     * @return 現在のトランザクションEnhancedClientDynamoDBTransaction
     */
    public static <T> DynamoDBEnhancedClientTransaction addUpdateItem(final MappedTableResource<T> mappedTableResource,
            final T item) {
        return getTransaction().addUpdateItem(mappedTableResource, item);
    }

    /**
     * DeleteItemの操作を現在のトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param key                 削除するキー
     * @return 現在のトランザクションEnhancedClientDynamoDBTransaction
     */
    public static <T> DynamoDBEnhancedClientTransaction addDeleteItem(final MappedTableResource<T> mappedTableResource,
            final Key key) {
        return getTransaction().addDeleteItem(mappedTableResource, key);
    }

}

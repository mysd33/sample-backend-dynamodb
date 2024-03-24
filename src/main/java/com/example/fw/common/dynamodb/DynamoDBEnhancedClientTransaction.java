package com.example.fw.common.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

/**
 * DynamoDbEnhancedClientを利用したDBトランザクションのクラスです。
 *
 */
public class DynamoDBEnhancedClientTransaction {
    private final TransactWriteItemsEnhancedRequest.Builder builder;
    private boolean hasTransactionItems = false;

    /**
     * コンストラクタ
     */
    public DynamoDBEnhancedClientTransaction() {
        builder = TransactWriteItemsEnhancedRequest.builder();
    }

    /**
     * ConditionCheckの操作をトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param conditionCheck      ConditionCheck
     * @return EnhancedClientDynamoDBTransaction
     */
    public <T> DynamoDBEnhancedClientTransaction addConditionCheck(MappedTableResource<T> mappedTableResource,
            ConditionCheck<T> conditionCheck) {
        builder.addConditionCheck(mappedTableResource, conditionCheck);
        return this;
    }

    /**
     * PutItemの操作をトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param item                登録するテーブルのアイテム
     * @return EnhancedClientDynamoDBTransaction
     */
    public <T> DynamoDBEnhancedClientTransaction addPutItem(MappedTableResource<T> mappedTableResource, T item) {
        builder.addPutItem(mappedTableResource, item);
        hasTransactionItems = true;
        return this;
    }

    /**
     * UpdateItemの操作をトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param item                更新するテーブルのアイテム
     * @return EnhancedClientDynamoDBTransaction
     */
    public <T> DynamoDBEnhancedClientTransaction addUpdateItem(MappedTableResource<T> mappedTableResource, T item) {
        builder.addUpdateItem(mappedTableResource, item);
        hasTransactionItems = true;
        return this;
    }

    /**
     * DeleteItemの操作をトランザクションに追加します。
     * 
     * @param <T>                 DynamoDbEnhancedClient利用する場合のテーブルのアイテムを表すクラス
     * @param mappedTableResource DynamoDbEnhancedClient利用する場合のテーブルクラス
     * @param key                 削除するキー
     * @return EnhancedClientDynamoDBTransaction
     */
    public <T> DynamoDBEnhancedClientTransaction addDeleteItem(MappedTableResource<T> mappedTableResource, Key key) {
        builder.addDeleteItem(mappedTableResource, key);
        hasTransactionItems = true;
        return this;
    }

    /**
     * 本トランザクションに登録されたTransactWriteItemsEnhancedRequestを返却します。
     * 
     * @return TransactWriteItemsEnhancedRequest
     */
    public TransactWriteItemsEnhancedRequest getTransactWriteItemsEnhancedRequest() {
        return builder.build();
    }

    /**
     * 本トランザクションにTransactWriteItemsの要求が登録されているかを返却します。
     * 
     * @return 登録されている場合はtrue
     */
    public boolean hasTransactionItems() {
        return hasTransactionItems;
    }

}

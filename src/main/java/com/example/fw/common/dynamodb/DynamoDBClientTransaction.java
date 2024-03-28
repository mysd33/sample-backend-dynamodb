package com.example.fw.common.dynamodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

/**
 * DynamoDbClientを利用したDBトランザクションのクラスです。
 */
public class DynamoDBClientTransaction {
    private final List<TransactWriteItem> transactWriteItems = new ArrayList<>();

    /**
     * 本トランザクションTransactWriteItemsの要求が登録されているかを返却します。
     * 
     * @return 登録されている場合はtrue
     */
    public boolean hasTransactionItems() {
        return !transactWriteItems.isEmpty();
    }

    /**
     * TransactWriteItemをトランザクションに追加します。
     * 
     * @param transactWriteItem
     * @return DynamoDBClientTransaction
     */
    public DynamoDBClientTransaction addTransactWriteItem(final TransactWriteItem transactWriteItem) {
        transactWriteItems.add(transactWriteItem);
        return this;
    }

    /**
     * TransactWriteItemsを返却します。
     * 
     * @return TransactWriteItems
     */
    public Collection<TransactWriteItem> getTransactWriteItems() {
        return transactWriteItems;
    }

}

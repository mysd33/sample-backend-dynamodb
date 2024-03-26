package com.example.fw.common.dynamodb;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * 複数のDynamoDBTransactionManagerをまとめるためのDynamoDBTransactionManagerクラス
 *
 */
@RequiredArgsConstructor
public class CompositeDynamoDBTransactionManager implements DynamoDBTransactionManager {
    private final List<DynamoDBTransactionManager> transactionManagers;
    
    @Override
    public void startTransaction() {    
        transactionManagers.forEach(tx -> tx.startTransaction());
    }

    @Override
    public void commit() {
        transactionManagers.forEach(tx -> tx.commit());
    }

    @Override
    public void rollback() {
        transactionManagers.forEach(tx -> tx.rollback());
    }
    
    @Override
    public void close() throws Exception {
        for (DynamoDBTransactionManager transactionManager : transactionManagers) {
            transactionManager.close();
        }
    }
}

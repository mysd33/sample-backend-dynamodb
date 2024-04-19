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
    	if (transactionManagers == null) {
    		return;
    	}
        transactionManagers.forEach(tx -> tx.startTransaction());
    }

    @Override
    public void commit() {
    	if (transactionManagers == null) {
    		return;
    	}
    	transactionManagers.forEach(tx -> tx.commit());
    }

    @Override
    public void rollback() {
    	if (transactionManagers == null) {
    		return;
    	}
        transactionManagers.forEach(tx -> tx.rollback());
    }
    
    @Override
    public void close() throws Exception {
    	if (transactionManagers == null) {
    		return;
    	}    	
        for (DynamoDBTransactionManager transactionManager : transactionManagers) {
            transactionManager.close();
        }
    }
}

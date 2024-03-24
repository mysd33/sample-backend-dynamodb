package com.example.fw.common.dynamodb;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;

/**
 * 
 * DynamoDbClientを利用したDynamoDBTransactionManagerの実装クラスです。
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DynamoDBClientTransactionManager implements DynamoDBTransactionManager {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    // トランザクションをスレッドローカルで管理
    private static final ThreadLocal<DynamoDBClientTransaction> transactionStore = new ThreadLocal<>();    
    private final DynamoDbClient dynamoDbClient;

    @Override
    public void startTransaction() {
        appLogger.debug("トランザクション開始");
        transactionStore.set(new DynamoDBClientTransaction());
        
    }

    @Override
    public void commit() {
        appLogger.debug("トランザクションコミット");
        DynamoDBClientTransaction tx = transactionStore.get();
        if (tx.hasTransactionItems()) {
            TransactWriteItemsResponse response = dynamoDbClient.transactWriteItems(r -> {
                r.transactItems(tx.getTransactWriteItems());
                if (appLogger.isDebugEnabled()) {
                    // ログがデバッグレベルの時には、キャパシティユニットをログ出力する
                    r.returnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
                }
            });
            for (ConsumedCapacity capacity : response.consumedCapacity()) {
                appLogger.debug("TransactWriteItems[{}]消費キャパシティユニット:{}", capacity.tableName(), capacity.capacityUnits());
            }                           
        }        
    }

    @Override
    public void rollback() {
        appLogger.debug("トランザクションロールバック");
        // 何もしない        
    }

    @Override
    public void endTransaction() {
        appLogger.debug("トランザクション終了");
        transactionStore.remove();        
    }

    /**
     * トランザクションオブジェクトを返却する
     * @return　トランザクションオブジェクト
     */
    public static DynamoDBClientTransaction getTransaction() {
        return transactionStore.get();
    }
}

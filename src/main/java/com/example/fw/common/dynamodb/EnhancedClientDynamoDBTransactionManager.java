package com.example.fw.common.dynamodb;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

/**
 * DynamoDbEnhancedClientを利用したDynamoDBTransactionManagerの実装クラスです。
 *
 */
@Slf4j
@RequiredArgsConstructor
public class EnhancedClientDynamoDBTransactionManager implements DynamoDBTransactionManager {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    // トランザクションをスレッドローカルで管理
    private static final ThreadLocal<EnhancedClientDynamoDBTransaction> transactionStore = new ThreadLocal<>();   
    private final DynamoDbEnhancedClient enhancedClient;    
    
    @Override
    public void startTransaction() {
        appLogger.debug("トランザクション開始");
        transactionStore.set(new EnhancedClientDynamoDBTransaction());        
    }

    @Override
    public void commit() {
        appLogger.debug("トランザクションコミット");
        EnhancedClientDynamoDBTransaction tx = transactionStore.get();
        if (tx.hasTransactionItems()) {
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
    public void endTransaction() {
        appLogger.debug("トランザクション終了");
        transactionStore.remove();
    }

    /**
     * トランザクションオブジェクトを返却する
     * @return　トランザクションオブジェクト
     */
    public static EnhancedClientDynamoDBTransaction getTransaction() {
        return transactionStore.get();
    }
    
}

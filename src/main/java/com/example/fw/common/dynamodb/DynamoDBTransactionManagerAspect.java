package com.example.fw.common.dynamodb;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import lombok.RequiredArgsConstructor;

/**
 * 
 * AOPでDynamoDBのトランザクション管理をするためのAspectクラス<br>
 * @DynamoDBTransactionalアノテーションに対してトランザクションの開始・終了を制御します。
 *
 */
@Aspect
@RequiredArgsConstructor
public class DynamoDBTransactionManagerAspect {        
    private final DynamoDBTransactionManager transactionManager;
        
    /**
     * トランザクションの開始・終了を実施します。
     * @param jp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.example.fw.common.dynamodb.DynamoDBTransactional)")    
    public Object aroundExecuteTransaction(final ProceedingJoinPoint jp) throws Throwable {
        try {
            // トランザクション開始
            transactionManager.startTransaction();
            // ビジネスロジック実行
            Object result = jp.proceed();            
            // トランザクションコミット
            transactionManager.commit();
            return result;
        } catch (Exception e) {
            // トランザクションロールバック
            transactionManager.rollback();
            throw e;
        } finally {
            // トランザクション終了
            transactionManager.endTransaction();
        }
    }
}

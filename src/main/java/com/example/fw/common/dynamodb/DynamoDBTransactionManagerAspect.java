package com.example.fw.common.dynamodb;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import lombok.RequiredArgsConstructor;

/// AOPでDynamoDBのトランザクション管理をするためのAspectクラス<br>
/// DynamoDBTransactionalアノテーションに対してトランザクションの開始・終了を制御します。
@Aspect
@RequiredArgsConstructor
public class DynamoDBTransactionManagerAspect {
    private final DynamoDBTransactionManager transactionManager;

    /// トランザクションの開始・終了を実施します。
    ///
    /// @param jp トランザクション処理対象
    /// @return 処理結果
    /// @throws Throwable 例外
    @Around("@annotation(com.example.fw.common.dynamodb.DynamoDBTransactional)")
    public Object aroundExecuteTransaction(final ProceedingJoinPoint jp) throws Throwable {
        try (transactionManager) {
            // トランザクション開始
            transactionManager.startTransaction();
            // ビジネスロジック実行
            Object result = jp.proceed();
            // トランザクションコミット
            transactionManager.commit();
            return result;
        } catch (Throwable e) {
            // トランザクションロールバック
            transactionManager.rollback();
            throw e;
        }
    }
}

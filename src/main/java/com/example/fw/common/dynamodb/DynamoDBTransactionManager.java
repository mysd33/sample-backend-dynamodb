package com.example.fw.common.dynamodb;

/**
 * 
 * DynamoDBのトランザクション管理機能を表すインタフェース
 *
 */
public interface DynamoDBTransactionManager extends AutoCloseable {
    /**
     * トランザクションを開始します。
     */
    void startTransaction();

    /**
     * コミットします。
     */
    void commit();

    /**
     * ロールバックします。
     */
    void rollback();

}

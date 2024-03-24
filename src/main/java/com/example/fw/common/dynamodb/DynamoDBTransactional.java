package com.example.fw.common.dynamodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DynamoDBTransactionalアノテーション<br>
 * DynamoDBのトランザクション管理（TransactWriteItems）を使用した場合に、本アノテーションを付与したメソッドがトランザクション境界となる。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamoDBTransactional {

}

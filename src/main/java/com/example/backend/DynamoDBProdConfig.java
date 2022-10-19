package com.example.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * DynamoDB 本番用の設定クラス
 *
 */
@Configuration
@Profile("production")	
public class DynamoDBProdConfig {
    
	//TODO: 本場用のBean定義
    
}

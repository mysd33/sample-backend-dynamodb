package com.example.fw.common.dynamodb;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import software.amazon.dynamodb.services.local.main.ServerRunner;
import software.amazon.dynamodb.services.local.server.DynamoDBProxyServer;

/**
 * DynamoDB Localを起動するクラス
 */
@RequiredArgsConstructor
public class DynamoDBLocalExecutor {
    private final int port;
    private final DynamoDBTableInitializer dynamoDBTableInitializer;
    private DynamoDBProxyServer server = null;

    /**
     * DynamoDB Local 起動
     * 
     * @throws Exception
     */
    @PostConstruct
    public void startup() throws Exception {
        // DynamoDB Local起動
        final String[] localArgs = { "-inMemory", "-port", String.valueOf(port), "-sharedDb" };
        server = ServerRunner.createServerFromCommandLineArgs(localArgs);
        server.start();

        // AP起動時動作確認用にテーブル作成
        dynamoDBTableInitializer.createTables();
    }

    /**
     * DynamoDB Local終了
     * 
     * @throws Exception
     */
    @PreDestroy
    public void shutdown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

}

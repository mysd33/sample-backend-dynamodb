package com.example.fw.common.dynamodb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

//import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
//import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * DynamoDB Localを起動するクラス
 */
@RequiredArgsConstructor
public class DynamoDBLocalExecutor {
    // TODO: DynamoDB Localがjakarta対応の予定なしのため、SpringBoot3での、DynamoDBLocalの組み込み起動が現状できない。
    //　以下の例のように、あらかじめDynamoDB Localを外部プロセスとして起動するしかない
    // java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 18000

    private final int port;
    private final DynamoDBTableInitializer dynamoDBTableInitializer;
//    private DynamoDBProxyServer server = null;
    @Setter
    private String nativeLibsPath = "native-libs";

    /**
     * DynamoDB Local 起動
     * 
     * @throws Exception
     */
    @PostConstruct
    public void startup() throws Exception {
/*
        // 特定のフォルダに出力したsqlite4java-win32-x64.dllのパスを通す
        System.setProperty("sqlite4java.library.path", nativeLibsPath);
        // DynamoDB Local起動
        final String[] localArgs = { "-inMemory", "-port", String.valueOf(port) };
        server = ServerRunner.createServerFromCommandLineArgs(localArgs);
        server.start();
*/
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
        /*
        if (server != null) {
            server.stop();
        }*/
    }

}

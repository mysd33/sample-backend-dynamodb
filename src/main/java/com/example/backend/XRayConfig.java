package com.example.backend;

import javax.servlet.Filter;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ECSPlugin;
import com.amazonaws.xray.plugins.EKSPlugin;
import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;

/**
 * X-Rayの設定クラス
 *
 */
@Profile("xray")
@Aspect
@Configuration
public class XRayConfig extends BaseAbstractXRayInterceptor {
    static {
        // サービスプラグインの設定
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EKSPlugin())
                .withPlugin(new ECSPlugin()).withPlugin(new EC2Plugin());
        // TODO: サンプリングルール
        // URL ruleFile = WebConfig.class.getResource("/sampling-rules.json");
        // builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));

        AWSXRay.setGlobalRecorder(builder.build());
    }

    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled) " + " && execution(* com.example..*.*(..))")
    protected void xrayEnabledClasses() {
    }

    /**
     * AWS X-Rayによる分散トレーシングの設定
     * 
     */
    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("sample-backend-dynamodb");
    }

}

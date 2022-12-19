package com.example.backend;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend.common.advice.ErrorResponseCreator;
import com.example.backend.domain.message.MessageIds;

import com.example.fw.web.aspect.LogAspect;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class WebConfig {
	
	/**
	 * エラーレスポンス作成クラス
	 */
	@Bean
	public ErrorResponseCreator errorResponseCreator(MessageSource messageSource) {
		return new ErrorResponseCreator(messageSource, MessageIds.W_EX_5001, MessageIds.E_EX_9001);
	}
	
	
	/**
	 * ロギング機能
	 */
	@Bean
	public LogAspect logAspect() {
		LogAspect logAspect = new LogAspect();
		logAspect.setDefaultExceptionMessageId(MessageIds.E_EX_9001);
		return logAspect;
	}

	/**
	 * Springdoc-openapiでスネークケースの設定が反映されるようにするための回避策
	 */
	@Bean
	public ModelResolver modelResolver(ObjectMapper objectMapper) {
	     return new ModelResolver(objectMapper);
	}
	
	/**
	 * Springdoc-openapiの定義
	 */
	@Bean
	  public OpenAPI springShopOpenAPI() {
	      return new OpenAPI()
	              .info(new Info().title("Todo APIドキュメント")
	              .description("Todo管理のためのAPIです。")
	              .version("v1.0"));
	}
}

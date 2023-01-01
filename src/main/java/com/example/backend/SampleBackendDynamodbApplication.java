package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.xray.AWSXRay;

@SpringBootApplication
public class SampleBackendDynamodbApplication {

	public static void main(String[] args) {
		AWSXRay.beginSegment("sample-backend-dynamodb");
		SpringApplication.run(SampleBackendDynamodbApplication.class, args);
	}

}

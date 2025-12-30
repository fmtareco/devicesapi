package com.example.devicesapi;

import com.example.devicesapi.security.ApiKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
//@EnableConfigurationProperties
//@ConfigurationPropertiesScan
@EnableConfigurationProperties(ApiKeyProperties.class)
public class DevicesapiApplication {

	public static void main(String[] args) {

        SpringApplication.run(DevicesapiApplication.class, args);
	}

}

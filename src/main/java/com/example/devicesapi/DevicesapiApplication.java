package com.example.devicesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class DevicesapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevicesapiApplication.class, args);
	}

}

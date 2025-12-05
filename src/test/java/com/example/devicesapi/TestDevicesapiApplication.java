package com.example.devicesapi;

import org.springframework.boot.SpringApplication;

public class TestDevicesapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(DevicesapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

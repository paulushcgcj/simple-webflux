package io.github.paulushcgcj.devopsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Company Service", version = "1.0.0", description = "Service responsible for managing companies"))
public class DevopsdemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DevopsdemoApplication.class, args);
	}

}

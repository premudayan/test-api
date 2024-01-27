package com.nextgendynamics.crm;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "NextGen Dynamics CRM",
				version = "1.0",
				description = "NextGen Dynamics CRM API",
				termsOfService = "http://www.nextgendynamics.com/terms",
				contact = @Contact(name = "admin", email = "admin@nextgendynamics.com"),
				license = @License(name = "License", url = "http://www.nextgendynamics.com/license")
		)
)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
public class CrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

}

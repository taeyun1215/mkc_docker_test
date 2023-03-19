package com.mck;

import com.mck.domain.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@EnableJpaAuditing
@EnableAspectJAutoProxy
@SpringBootApplication
public class MckApplication {

	public static void main(String[] args) {
		SpringApplication.run(MckApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
//			userService.saveRole(new Role(null, "ROLE_USER"));
//			userService.saveRole(new Role(null, "ROLE_ADMIN"));
		};
	}

}

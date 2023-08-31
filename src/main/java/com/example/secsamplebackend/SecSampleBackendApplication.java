package com.example.secsamplebackend;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.oauth2.domain.entity.RefreshToken;
import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import com.example.secsamplebackend.oauth2.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan("com.example.secsamplebackend.config.envs")
public class SecSampleBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecSampleBackendApplication.class, args);
	}


}

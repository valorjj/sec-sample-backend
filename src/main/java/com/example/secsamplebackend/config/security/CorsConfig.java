package com.example.secsamplebackend.config.security;

import com.example.secsamplebackend.config.envs.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorsConfig {

	private final CorsProperties corsProperties;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE
		// configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
		configuration.addAllowedOriginPattern("http://localhost:[*]");
		configuration.setAllowCredentials(true);
		configuration.addExposedHeader("Authorization");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


}

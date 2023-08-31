package com.example.secsamplebackend.config.security;

import com.example.secsamplebackend.api.common.filter.HeaderFilter;
import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author : Jeongjin Kim
 * @fileName : WebConfig
 * @since : 2023.08.23
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:5173")
				.allowedHeaders("*")
				.allowedMethods("*")
				.allowCredentials(true)
				.maxAge(1800);
	}


}

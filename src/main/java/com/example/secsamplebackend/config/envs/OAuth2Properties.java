package com.example.secsamplebackend.config.envs;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

	private List<String> authorizedRedirectUris;
}

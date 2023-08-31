package com.example.secsamplebackend.config.envs;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	// WARNING: Base64 으로 인코딩 한 뒤 사용
	private String jwtSecretKey;
	// 'ROLE'
	private String authoritiesKey;
	// 'refresh'
	private String cookieRefreshTokenKey;
	// expiration, header
	private AccessToken accessToken;
	// expiration, header
	private RefreshToken refreshToken;

	@Data
	public static class AccessToken {
		private Long expiration;
		private String header;
	}

	@Data
	public static class RefreshToken {
		private Long expiration;
		private String header;
	}

}

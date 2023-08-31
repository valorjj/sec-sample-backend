package com.example.secsamplebackend.oauth2.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.secsamplebackend.config.envs.JwtProperties;
import com.example.secsamplebackend.oauth2.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.example.secsamplebackend.oauth2.contant.AuthTokenConstant.*;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class JwtService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtProperties jwtProperties;


	public String createAccessToken(String email) {
		Date now = new Date();

		return JWT.create()
				.withSubject(ACCESS_TOKEN_SUBJECT)
				.withExpiresAt(new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration()))
				.withClaim(EMAIL_CLAIM, email)
				.sign(Algorithm.HMAC512(createSignature()));
	}

	public String createRefreshToken() {
		Date now = new Date();

		return JWT.create()
				.withSubject(REFRESH_TOKEN_SUBJECT)
				.withExpiresAt(new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration()))
				.sign(Algorithm.HMAC512(createSignature()));
	}

	/**
	 * AccessToken 헤더에 담아서 전송합니다.
	 *
	 * @param response
	 * @param accessToken
	 */
	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader(jwtProperties.getAccessToken().getHeader(), accessToken);
		log.info("[+] 재발급된 AccessToken := [{}]", accessToken);
	}

	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);
		log.info("[+] AccessToken, RefreshToken 발급 완료");
	}

	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(jwtProperties.getRefreshToken().getHeader()))
				.filter(refreshToken -> refreshToken.startsWith(TOKEN_TYPE))
				.map(refreshToken -> refreshToken.replace(TOKEN_TYPE, ""));
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(jwtProperties.getAccessToken().getHeader()))
				.filter(refreshToken -> refreshToken.startsWith(TOKEN_TYPE))
				.map(refreshToken -> refreshToken.replace(TOKEN_TYPE, ""));
	}

	public Optional<String> extractEmail(String accessToken) {
		try {
			return Optional.ofNullable(JWT.require(Algorithm.HMAC512(jwtProperties.getJwtSecretKey()))
					.build()
					.verify(accessToken)
					.getClaim(EMAIL_CLAIM)
					.asString()
			);
		} catch (Exception e) {
			log.error("[x] 유효하지 않은 AccessToken 입니다.");
			return Optional.empty();
		}
	}

	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(jwtProperties.getAccessToken().getHeader(), accessToken);
	}

	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(jwtProperties.getRefreshToken().getHeader(), refreshToken);
	}

	public void updateRefreshToken(String email, String refreshToken) {
	}

	public Boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(jwtProperties.getJwtSecretKey())).build().verify(token);
			return true;
		} catch (IllegalArgumentException e) {
			log.info("[+] 잘못된 입력 값 입니다.:= [{}]", e.getMessage());
			return false;
		} catch (SignatureVerificationException e) {
			log.info("[+] 잘못된 서명입니다. := [{}]", e.getMessage());
			return false;
		} catch (TokenExpiredException e) {
			log.info("[+] 만료된 토큰입니다. := [{}]", e.getMessage());
			return false;
		} catch (AlgorithmMismatchException e) {
			log.info("[+] 서명에 사용된 알고리즘이 일치하지 않습니다. := [{}]", e.getMessage());
			return false;
		} catch (Exception e) {
			log.info("[+] 에러가 발생했습니다. := [{}]", e.getMessage());
			return false;
		}
	}

	public byte[] createSignature() {
		return Base64.encodeBase64(jwtProperties.getJwtSecretKey().getBytes());
	}


}

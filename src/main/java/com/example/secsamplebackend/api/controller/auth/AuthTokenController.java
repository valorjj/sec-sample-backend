package com.example.secsamplebackend.api.controller.auth;

import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import com.example.secsamplebackend.oauth2.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Jeongjin Kim
 * @fileName : WebConfig
 * @since : 2023.08.23
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class AuthTokenController {

	private final RefreshTokenService refreshTokenService;

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String accessToken
	) {
		return ResponseEntity.ok().body(refreshTokenService.updateRefreshToken(request, response, accessToken));
	}


}

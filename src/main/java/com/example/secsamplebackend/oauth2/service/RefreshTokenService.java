package com.example.secsamplebackend.oauth2.service;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.config.envs.JwtProperties;
import com.example.secsamplebackend.oauth2.domain.CustomUserDetails;
import com.example.secsamplebackend.oauth2.domain.entity.RefreshToken;
import com.example.secsamplebackend.oauth2.repository.RefreshTokenRepository;
import com.example.secsamplebackend.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

	private final JwtProperties jwtProperties;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthTokenService authTokenService;

	/**
	 * @param request
	 * @param response
	 * @param oldAccessToken
	 * @return
	 */
	public String updateRefreshToken(HttpServletRequest request, HttpServletResponse response, String oldAccessToken) {
		String result = null;
		// 1. 쿠키에서 refresh_token 을 추출합니다.
		String oldRefreshToken = CookieUtils.getCookie(request, jwtProperties.getCookieRefreshTokenKey())
				.map(Cookie::getValue).orElseThrow(() -> new CustomApiException("쿠키에 refresh_token 이 존재하지 않습니다."));
		// 2. refresh_token 이 유효한지 검사합니다.
		if (!authTokenService.isValidAuthToken(oldRefreshToken)) {
			throw new CustomApiException("유효한 refresh_token 이 아닙니다.");
		}
		// 3. access_token 으로부터 유저 정보를 획득합니다.
		Authentication authentication = authTokenService.getAuthentication(oldAccessToken);
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Long userSeq = userDetails.getIdx();
		// 4. 유저 식별값으로 DB 에 저장된 refresh_token 존재 여부를 확인합니다.
		Optional<RefreshToken> refreshTokenByUserId = refreshTokenRepository.getRefreshTokenByUserIdx(userSeq);
		if (refreshTokenByUserId.isPresent()) {
			String savedRefreshToken = refreshTokenByUserId.get().getRefreshToken();
			// 4.1.if. 쿠키에서 추출한 값과 불일치하는 경우 에러를 발생시킵니다.
			if (!savedRefreshToken.equals(oldRefreshToken)) {
				throw new CustomApiException("refresh_token 이 존재하지 않습니다.");
			}
			// 4.1.else. 쿠키에서 추출한 값과 일치하는 경우 토큰을 갱신합니다.
			else {
				String newAccessToken = authTokenService.createAccessToken(authentication);
				authTokenService.createRefreshToken(authentication, response);

				result = newAccessToken;
			}
		}
		// 5. 위의 어느 조건에도 해당하지 않는 경우 null 을 리턴합니다.
		return result;
	}


}

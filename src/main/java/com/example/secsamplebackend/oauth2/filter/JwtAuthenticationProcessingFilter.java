package com.example.secsamplebackend.oauth2.filter;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.service.user.UserServiceImpl;
import com.example.secsamplebackend.oauth2.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 과정을 담당하는 필터
 * NO_CHECK_URLS 에서 지정한 URL 은 인증 과정 없이 통과시킵니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final String[] NO_CHECK_URLS = {
			"/login",
			"/indexPage",
			"/google/login",
			"/"
	};

	private final JwtService jwtService;
	private final UserServiceImpl userService;

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		for (String url : NO_CHECK_URLS) {
			if (request.getRequestURI().equalsIgnoreCase(url)) {
				filterChain.doFilter(request, response);
				return;
			}
		}

		String extractedRefreshToken = jwtService.extractRefreshToken(request)
				.filter(jwtService::isTokenValid)
				.orElse(null);

		if (extractedRefreshToken != null) {
			checkRefreshTokenAndReissueAccessToken(response, extractedRefreshToken);
			return;
		} else {
			checkAccessTokenAndAuthentication(request, response, filterChain);
		}


	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param filterChain
	 */
	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {




	}

	/**
	 * Refresh Token 으로 유저 정보를 찾고, Access Token 과 Refresh Token 을 재발급하는 메서드
	 *
	 * @param response
	 * @param extractedRefreshToken
	 */
	private void checkRefreshTokenAndReissueAccessToken(HttpServletResponse response, String extractedRefreshToken) {


	}

	private String reissueRefreshToken(User user) {

		return null;
	}

	public void saveAuthentication(User user) {

	}

}

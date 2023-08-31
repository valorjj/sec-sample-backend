package com.example.secsamplebackend.oauth2.filter;

import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.secsamplebackend.oauth2.contant.AuthTokenConstant.*;

@Slf4j
// @Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	// private static final String[] NO_AUTH_CHECK_URLS = {
	// 		"/login",
	// 		"/indexPage",
	// };

	private final AuthTokenService authTokenService;

	/**
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		log.info("JwtAuthenticationFilter 접근");
		// 1. NO_AUTH_CHECK_URLS 에 해당하는 요청은 인증 필터를 거치지 않게 합니다.
		// for (String url : NO_AUTH_CHECK_URLS) {
		// 	if (request.getRequestURI().equals(url)) {
		// 		filterChain.doFilter(request, response);
		// 		return;
		// 	}
		// }

		// 1. request 로 부터 인증 토큰을 획득합니다.
		String bearerToken = parseBearerToken(request);
		// 2.if. 인증 토큰이 유효한 경우입니다.
		if (StringUtils.hasText(bearerToken) && authTokenService.validateAuthToken(bearerToken)) {
			// 2.1. 토큰으로부터 인증 객체를 생성합니다.
			Authentication authentication = authTokenService.getAuthentication(bearerToken);
			// 2.2. 인증 객체를 SecurityContextHolder 에 저장합니다.
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("[*] 인증 정보의 소유자 := [{}]", authentication.getName());
		}
		// 2.else. 인증 토큰이 유효하지 않은 경우입니다.
		else {
			// 2.3.
			log.debug("[*] 유효한 JWT 토큰이 아닙니다.");
		}
		// 3.
		filterChain.doFilter(request, response);
	}

	/**
	 * request 로 부터 Bearer Token 을 추출합니다.
	 *
	 * @param request
	 * @return
	 */
	private String parseBearerToken(HttpServletRequest request) {
		// 1. HTTP 헤더에 포함된 Authorization 값을 추출합니다.
		String bearerTokenFromHeader = request.getHeader(AUTH_HEADER);
		// 2. 해당 값이 Bearer 토큰임을 확인합니다.
		if (StringUtils.hasText(bearerTokenFromHeader) && bearerTokenFromHeader.startsWith(TOKEN_TYPE)) {
			// 2.1 'Bearer ' 값을 제외한 실제 토큰 값을 추출해서 반환합니다.
			return bearerTokenFromHeader.substring(7);
		}
		return null;
	}

}

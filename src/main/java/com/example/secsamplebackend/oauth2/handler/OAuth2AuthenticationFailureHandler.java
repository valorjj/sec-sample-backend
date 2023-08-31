package com.example.secsamplebackend.oauth2.handler;

import com.example.secsamplebackend.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.secsamplebackend.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.example.secsamplebackend.oauth2.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;


/**
 * OAuth 2.0 을 통해서 인증 과정이 실패한 경우를 담당합니다.
 *
 * @author Jeongjin Kim
 * @fileName OAuth2AuthenticationFailureHandler
 * @since 2023.08.22
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

	/**
	 * @param request   the request during which the authentication attempt occurred.
	 * @param response  the response.
	 * @param exception the exception which was thrown to reject the authentication
	 *                  request.
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		// 1. REDIRECT_URI_PARAM_COOKIE_NAME 를 가진 쿠키의 값을 가져옵니다.
		String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue)
				.orElse("/");
		log.debug("[*] targetUrl := [{}]", targetUrl);
		// 2. 응답할 url 에 error 를 포함시킵니다.
		targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("error", exception.getLocalizedMessage())
				.build().toString();
		// 3. 인가서버와 통신에서 사용된 쿠키를 제거합니다.
		cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
		// 4. targetUrl 로 redirect 시킵니다.
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}



package com.example.secsamplebackend.oauth2.repository;

import com.example.secsamplebackend.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * 인가서버와의 인가 과정 중, {@link OAuth2AuthorizationRequest} 를 {@link jakarta.servlet.http.Cookie} 에 저장하기 위한 클래스입니다.
 *
 * <p>
 * 사용자 정보를 요청하는 과정에서 2개의 쿠키를 사용합니다.
 * {@code OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME} 는 인가서버로 요청을 보낼 때 사용합니다. {@code cookie} 를
 * 식별하기 위한 고유한 값 입니다. </p>
 * <p>
 * {@code REDIRECT_URI_PARAM_COOKIE_NAME} 는 인가서버에서 받아온 사용자 정보를 담을 때 사용합니다. 해당 {@code cookie}
 * 에는 {@code redirect_uri} 가 담겨있습니다. {@code application.yml} 에 설정한 값과 동일한 경우에만 인증이 진행됩니다.
 * ex) http://localhost:8081/oauth2/authorize/google?redirect_uri=http://localhost:5143/oauth2/redirect
 * <p/>
 *
 * @author Jeongjin Kim
 * @fileName CookieAuthorizationRequestRepository
 * @since 2023.08.22
 */
@Slf4j
public class CookieAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	// 인가서버와의 인가과정 중 authorization_request 를 쿠키에 저장할 때 고유한 key 값으로 사용됩니다.
	public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
	// authorization_request 시 param 으로 넘어온 redirect_uri 를
	public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
	// 쿠키의 유효시간은 180초로 설정합니다.
	private static final int COOKIE_EXPIRE_SECONDS = 180;

	/**
	 * @param request the {@code HttpServletRequest}
	 * @return
	 */
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		log.info("[+] CookieAuthorizationRequestRepository 접근");
		return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
				.map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
				.orElse(null);
	}

	/**
	 * @param authorizationRequest the {@link OAuth2AuthorizationRequest}
	 * @param request              the {@code HttpServletRequest}
	 * @param response             the {@code HttpServletResponse}
	 */
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
			CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
		} else {
			CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
			String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
			if (!StringUtils.hasText(redirectUriAfterLogin)) {
				CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
			}
		}
	}


	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
		return this.loadAuthorizationRequest(request);
	}

	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
	}


}

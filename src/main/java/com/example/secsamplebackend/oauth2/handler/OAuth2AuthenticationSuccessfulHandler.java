package com.example.secsamplebackend.oauth2.handler;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.oauth2.domain.CustomUserDetails;
import com.example.secsamplebackend.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import com.example.secsamplebackend.oauth2.service.JwtService;
import com.example.secsamplebackend.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.example.secsamplebackend.oauth2.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;


/**
 * OAuth 2.0 을 통해서 인증 과정이 성공한 경우 해당 객체를 거칩니다.
 *
 * @author Jeongjin Kim
 * @fileName OAuth2AuthenticationSuccessfulHandler
 * @since 2023.08.22
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessfulHandler extends SimpleUrlAuthenticationSuccessHandler {

	private String redirectUri;
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		log.info("[+] 인증 성공");
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String email = userDetails.getEmail();
		String accessToken = jwtService.createAccessToken(email);
		String refreshToken = jwtService.createRefreshToken();


		String targetUrl = determineTargetUrl(request, response, authentication);

		String uriString = UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("access", accessToken)
				.queryParam("refresh", refreshToken)
				.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, uriString);
	}


	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new CustomApiException("Redirect URI 가 일치하지 않습니다.");
		}
		clearAuthenticationAttributes(request, response);
		return redirectUri.orElse(getDefaultTargetUrl());
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
	}

	private Boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		URI authorizedUri = URI.create(redirectUri);

		return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
				&& authorizedUri.getPort() == clientRedirectUri.getPort();
	}
}
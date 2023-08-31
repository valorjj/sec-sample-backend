package com.example.secsamplebackend.oauth2.service;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.common.exception.custom.CustomAssert;
import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.api.service.user.UserServiceImpl;
import com.example.secsamplebackend.oauth2.domain.CustomUserDetails;
import com.example.secsamplebackend.oauth2.domain.OAuth2UserInfo;
import com.example.secsamplebackend.oauth2.domain.OAuth2UserInfoFactory;
import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;


/**
 * OAuth 2.0 로그인 시, 인가서버로부터 OpenID Connect Protocol 로 받아온 유저 정보를 처리합니다.
 * OidcUser 에 유저 정보가 맵핑됩니다.
 *
 * @author Jeongjin Kim
 * @fileName CustomOidcUserService
 * @since 2023.08.24
 */
@Component
@Slf4j
public class CustomOidcUserService extends AbstractOAuth2Service implements OAuth2UserService<OidcUserRequest, OidcUser> {

	private final UserServiceImpl userService;

	public CustomOidcUserService(UserRepository userRepository, UserServiceImpl userService) {
		super(userRepository);
		this.userService = userService;
	}

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("[+] CustomOidcUserService 접근");

		// 1.
		OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = new OidcUserService();

		return process(userRequest, oidcUserService.loadUser(userRequest));
	}


	public OidcUser process(OidcUserRequest userRequest, OidcUser oidcUser) {
		// 1.
		ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
		log.debug("[*] providerType := [{}]", providerType);
		// 2.
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oidcUser.getAttributes());
		CustomAssert.notEmptyString(oAuth2UserInfo.getEmail(), "유효하지 않은 사용자 정보입니다.", CustomApiException.class);
		// 3.
		User user = userService.findUserByEmail(oAuth2UserInfo.getEmail());
		// 4-if.
		if (user != null) {
			// 4.1.
			if (providerType != user.getProviderType()) {
				throw new CustomApiException("Provider 가 일치하지 않습니다.");
			}
			// 4.2.
			user = super.updateUser(user, oAuth2UserInfo);
		}
		// 4-else.
		else {
			// 4.3
			user = super.createUser(oAuth2UserInfo, providerType);
		}
		// 5.
		return CustomUserDetails.create(user, oAuth2UserInfo.getAttributes());
	}


}

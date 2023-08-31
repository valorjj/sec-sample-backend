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
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


/**
 * OAuth 2.0 로그인 시, 인가서버로 부터 받아온 유저 정보를 처리합니다.
 * OAuth2User 에 유저 정보가 맵핑됩니다.
 *
 * @author Jeongjin Kim
 * @fileName CustomOAuth2UserService
 * @since 2023.08.24
 */
@Slf4j
@Service
public class CustomOAuth2UserService extends AbstractOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	// TODO: UserServiceImpl 이 로직을 처리하도록 변경
	private final UserServiceImpl userService;

	public CustomOAuth2UserService(UserRepository userRepository, UserServiceImpl userService) {
		super(userRepository);
		this.userService = userService;
	}

	/**
	 * @param userRequest the user request
	 * @return
	 * @throws {@link OAuth2AuthenticationException}
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 1.
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		// 2.
		return process(userRequest, oAuth2UserService.loadUser(userRequest));
	}

	/**
	 * 인가서버로부터 받아온 정보로 DB 에 저장
	 *
	 * @param request    인가서버가 전송한 사용자 정보를 담는 객체
	 * @param oAuth2User OAuth2.0 로 등록한 사용자 인증 정보
	 * @return {@code OAuth2User}
	 */
	private OAuth2User process(OAuth2UserRequest request, OAuth2User oAuth2User) {
		// 1.
		ProviderType providerType = ProviderType.valueOf(request.getClientRegistration().getRegistrationId().toUpperCase());
		// 2.
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());
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
			// 4.3.
			user = super.createUser(oAuth2UserInfo, providerType);
		}
		// 5.
		return CustomUserDetails.create(user, oAuth2User.getAttributes());
	}


}

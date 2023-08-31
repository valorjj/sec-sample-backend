package com.example.secsamplebackend.oauth2.service;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.oauth2.domain.OAuth2UserInfo;
import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@RequiredArgsConstructor
public abstract class AbstractOAuth2Service {

	private final UserRepository userRepository;

	/**
	 * 인가서버에서 받아온 사용자 정보로 {@link User} 객체를 생성
	 *
	 * @param oAuth2UserInfo {@link OAuth2UserInfo}
	 * @param providerType   {@link ProviderType}
	 * @return
	 */
	public User createUser(OAuth2UserInfo oAuth2UserInfo, ProviderType providerType) {
		// 1.
		User user = User.builder()
				.email(oAuth2UserInfo.getEmail())
				.userId(oAuth2UserInfo.getId())
				.providerType(providerType)
				.roleType(RoleType.USER)
				.profileImageUrl(oAuth2UserInfo.getImageUrl())
				.build();
		// 2.
		return userRepository.save(user);
	}

	/**
	 * 기존에 존재하는 유저이면 정보를 update 합니다.
	 *
	 * @param existingUser   {@link User}
	 * @param oAuth2UserInfo {@link OAuth2UserInfo}
	 * @return
	 */
	public User updateUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
		// 1.
		existingUser.setUsername(oAuth2UserInfo.getName());
		existingUser.setEmail(oAuth2UserInfo.getEmail());
		// 2.
		return userRepository.save(existingUser);
	}

}

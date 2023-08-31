package com.example.secsamplebackend.oauth2.domain;

import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import com.example.secsamplebackend.oauth2.domain.social.GithubOAuth2UserInfo;
import com.example.secsamplebackend.oauth2.domain.social.GoogleOAuth2UserInfo;
import com.example.secsamplebackend.oauth2.domain.social.KakaoOAuth2UserInfo;
import com.example.secsamplebackend.oauth2.domain.social.NaverOAuth2UserInfo;
import com.example.secsamplebackend.api.common.exception.custom.CustomUnauthorizedException;

import java.util.Map;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
		return switch (providerType) {
			case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
			case NAVER -> new NaverOAuth2UserInfo(attributes);
			case KAKAO -> new KakaoOAuth2UserInfo(attributes);
			case GITHUB -> new GithubOAuth2UserInfo(attributes);
			default -> throw new CustomUnauthorizedException("Invalid Provider Type.");
		};
	}

}

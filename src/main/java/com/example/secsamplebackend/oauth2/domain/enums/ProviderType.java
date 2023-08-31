package com.example.secsamplebackend.oauth2.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType {

	GOOGLE("google"),
	GITHUB("github"),
	NAVER("naver"),
	KAKAO("kakao"),
	KEYCLOAK("keycloak"),
	LOCAL("local");

	private final String providerType;

}

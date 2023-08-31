package com.example.secsamplebackend.oauth2.domain.social;

import com.example.secsamplebackend.oauth2.domain.OAuth2UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


public class NaverOAuth2UserInfo extends OAuth2UserInfo {
	Map<String, Object> naverAttr;

	public NaverOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
		ObjectMapper objectMapper = new ObjectMapper();
		naverAttr = objectMapper.convertValue(attributes.get("properties"), new TypeReference<>() {
		});
	}

	@Override
	public String getId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getName() {
		return (String) naverAttr.get("nickname");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("account_email");
	}

	@Override
	public String getImageUrl() {
		return (String) naverAttr.get("thumbnail_image");
	}
}

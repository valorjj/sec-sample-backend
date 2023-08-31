package com.example.secsamplebackend.oauth2.domain.social;

import com.example.secsamplebackend.oauth2.domain.OAuth2UserInfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
	public GithubOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	/**
	 * @return
	 */
	@Override
	public String getId() {
		return null;
	}

	/**
	 * @return
	 */
	@Override
	public String getName() {
		return null;
	}

	/**
	 * @return
	 */
	@Override
	public String getEmail() {
		return null;
	}

	/**
	 * @return
	 */
	@Override
	public String getImageUrl() {
		return null;
	}
}

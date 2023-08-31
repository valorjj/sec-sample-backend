package com.example.secsamplebackend.util;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.common.exception.custom.CustomAssert;
import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtils {

	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String AUTH_TOKEN_PREFIX = "Bearer ";

	public static String getAccessToken(HttpServletRequest request) {
		String header = request.getHeader(HEADER_AUTHORIZATION);
		CustomAssert.notNull(header, "HTTP Header 에 Authorization 값이 존재하지 않습니다.", CustomApiException.class);

		if (header.startsWith(AUTH_TOKEN_PREFIX)) {
			return header.substring(AUTH_TOKEN_PREFIX.length());
		}
		return null;
	}

}

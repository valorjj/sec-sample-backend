package com.example.secsamplebackend.oauth2.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleType {

	USER("ROLE_USER", "일반 사용자 권한"),
	ADMIN("ROLE_ADMIN", "관리자 권한"),
	MASTER("ROLE_MASTER", "마스터 권한"),
	GUEST("ROLE_GUEST", "게스트 권한");

	private final String role;
	private final String displayName;

	public static RoleType of(String code) {
		return Arrays.stream(RoleType.values())
				.filter(r -> r.getRole().equals(code))
				.findAny()
				.orElse(GUEST);
	}

}

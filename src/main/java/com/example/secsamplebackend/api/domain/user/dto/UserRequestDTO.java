package com.example.secsamplebackend.api.domain.user.dto;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

public class UserRequestDTO {

	@Data
	public static class UserSignupRequestDTO {
		@NotEmpty
		private String email;
		@NotEmpty
		private RoleType roleType;
		private String username;
		private String password;

		@Builder
		public UserSignupRequestDTO(String username, String email, String password, RoleType roleType) {
			this.username = username;
			this.email = email;
			this.password = password;
			this.roleType = roleType;
		}

		@Builder
		public UserSignupRequestDTO(String email, String password, RoleType roleType) {
			this.email = email;
			this.password = password;
			this.roleType = roleType;
		}

		@Builder
		public UserSignupRequestDTO(String email, String password) {
			this.email = email;
			this.password = password;
		}

		public User to() {
			return User.builder()
					.username(this.username)
					.email(this.email)
					.roleType(this.roleType)
					.build();
		}

	}


	@Data
	public static class OAuth2UserRequestDTO {
		private String userId;
		private String username;
		private String email;
		private ProviderType providerType;
		private RoleType roleType;

		@Builder
		public OAuth2UserRequestDTO(String userId, String username, String email, ProviderType providerType, RoleType roleType) {
			this.userId = userId;
			this.username = username;
			this.email = email;
			this.providerType = providerType;
			this.roleType = roleType;
		}

		@Builder
		public OAuth2UserRequestDTO(String username, String email, ProviderType providerType, RoleType roleType) {
			this.username = username;
			this.email = email;
			this.providerType = providerType;
			this.roleType = roleType;
		}
	}

}

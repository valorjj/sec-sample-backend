package com.example.secsamplebackend.api.domain.user.dto;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

public class UserResponseDTO {

	@Data
	public static class UserSignupResponseDTO {
		@NotEmpty
		private String email;
		@NotEmpty
		private RoleType roleType;
		private String username;

		@Builder
		public UserSignupResponseDTO(User user) {
			this.username = user.getUsername();
			this.email = user.getEmail();
			this.roleType = user.getRoleType();
		}
	}

	@Data
	public static class OAuth2UserResponseDTO {
		private Long userSeq;
		private String userId;
		private String username;
		private String email;

		@Builder
		public OAuth2UserResponseDTO(User user) {
			this.userSeq = user.getIdx();
			this.userId = user.getUserId();
			this.username = user.getUsername();
		}
	}


}

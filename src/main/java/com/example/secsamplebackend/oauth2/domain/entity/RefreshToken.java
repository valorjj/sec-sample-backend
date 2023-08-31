package com.example.secsamplebackend.oauth2.domain.entity;


import com.example.secsamplebackend.api.common.entity.BaseEntity;
import com.example.secsamplebackend.api.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "REFRESH_TB")
@Entity
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REFRESH_TOKEN_IDX")
	@JsonIgnore
	private Long idx;

	@Column(name = "REFRESH_TOKEN", nullable = false)
	private String refreshToken;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_IDX")
	private User user;


	@Builder
	public RefreshToken(User user, String refreshToken) {
		this.user = user;
		this.refreshToken = refreshToken;
	}

	public void setUser(User user) {
		// 기존 연관 관계를 제거합니다.
		if (this.user != null) {
			this.user.getRefreshTokenList().remove(this);
		}
		// 새로운 연관 관계를 설정합니다.
		this.user = user;
		if (user != null) {
			user.getRefreshTokenList().add(this);
		}
	}

}

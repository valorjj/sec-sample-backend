package com.example.secsamplebackend.api.domain.user.entity;

import com.example.secsamplebackend.api.common.entity.BaseEntity;
import com.example.secsamplebackend.oauth2.domain.entity.RefreshToken;
import com.example.secsamplebackend.oauth2.domain.enums.ProviderType;
import com.example.secsamplebackend.oauth2.domain.enums.RoleType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_TB")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_IDX")
	private Long idx;

	/*
	 * 인가서버에 등록된 사용자 식별값
	 * OIDC 에서 sub 에 해당
	 * form 으로 로그인 하는 경우 존재하지 않는 값이다.
	 * */
	@Nullable
	@Column(name = "USER_ID")
	private String userId;

	/*
	 * 인가서버에 등록된 사용자 이름
	 * name, nickname, username 등 다양한 attribute 의 key 값으로 받아오는 값
	 * 소셜 로그인 동의 항목에 포함되어 있지 않다면 존재하지 않는다.
	 * */
	@Nullable
	@Column(name = "USERNAME")
	private String username;

	/*
	 * form 으로 가입한 회원만 가지고 있는 값
	 * 인가서버에서 비밀번호 항목은 가져오지 않는다.
	 * */
	@Nullable
	@Column(name = "PASSWORD")
	private String password;

	/*
	 * 인가서버에서 받아온 회원의 이메일 주소
	 * 소셜 로그인 동의 항목에 포함되어 있지 않다면 존재하지 않는다.
	 *
	 * */
	@Nullable
	@Column(name = "EMAIL", length = 64)
	@Size(min = 4, max = 64)
	private String email;

	/*
	 * 인가서버에서 받아온 회원 프로필 사진 url
	 * 소셜 로그인 동의 항목에 포함되어 있지 않다면 존재하지 않는다.
	 *
	 * */
	@Column(name = "PROFILE_IMAGE_URL")
	private String profileImageUrl;


	/*
	 * 인가서버의 종류
	 * google, github, naver, kakao, facebook
	 * */
	@Enumerated(EnumType.STRING)
	private ProviderType providerType;

	/*
	 * 서비스를 이용하는 회원에게 부여되는 권한의 종류
	 * ROLE_GUEST, ROLE_USER, ROLE_ADMIN, ROLE_MASTER
	 * 해당 권한으로 자원에 대한 접근을 제어한다.
	 * */
	@Enumerated(EnumType.STRING)
	private RoleType roleType;

	@OneToMany(mappedBy = "user", targetEntity = RefreshToken.class, cascade = CascadeType.ALL)
	private List<RefreshToken> refreshTokenList = new ArrayList<>();


	@Builder
	public User(
			@Nullable String userId,
			@Nullable String username,
			@Nullable String email,
			@Nullable String profileImageUrl,
			ProviderType providerType,
			RoleType roleType
	) {
		this.userId = userId != null ? userId : "";
		this.username = username != null ? username : "";
		this.password = "NONE";
		this.email = email != null ? email : "NONE";
		this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "";
		this.providerType = providerType != null ? providerType : ProviderType.LOCAL;
		this.roleType = roleType != null ? roleType : RoleType.USER;
	}

}

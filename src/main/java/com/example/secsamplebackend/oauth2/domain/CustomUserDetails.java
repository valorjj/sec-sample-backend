package com.example.secsamplebackend.oauth2.domain;

import com.example.secsamplebackend.api.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User, OidcUser {

	// 인가서버가 전송하는 사용자 식별값
	private final Long idx;
	// 인가서버가 전송하는 사용자의 이메일
	private final String email;
	// 인가서버가 전송하는 사용자의 이름
	private final String username;
	// 인가서버가 전송하는 사용자의 권한
	private final Collection<? extends GrantedAuthority> authorities;
	// 인가서버가 전송하는 사용자의 특성
	private Map<String, Object> attributes;

	public CustomUserDetails(Long idx, String email, String username, Collection<? extends GrantedAuthority> authorities) {
		this.idx = idx;
		this.email = email;
		this.username = username;
		this.authorities = authorities;
	}

	/**
	 * {@code CustomUserDetails} 의 생성자
	 *
	 * @param user {@code User} 객체
	 * @param attributes 사용자 정보
	 * @return {@code CustomUserDetails}
	 */
	public static CustomUserDetails create(User user, Map<String, Object> attributes) {
		CustomUserDetails userDetails = CustomUserDetails.create(user);
		userDetails.setAttributes(attributes);
		return userDetails;
	}

	/**
	 * {@code CustomUserDetails} 의 생성자
	 *
	 * @param user CustomUserDetails
	 * @return 'ROLE_USER' {@code RoleType} 을 부여한 {@code CustomUserDetails}
	 */
	public static CustomUserDetails create(User user) {
		List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		return new CustomUserDetails(user.getIdx(), user.getEmail(), user.getUsername(), grantedAuthorities);
	}

	/**
	 * @return 인가서버에서 받아오는 유저의 고유한 번호, 식별값
	 */
	@Override
	public String getName() {
		return String.valueOf(idx);
	}


	@Override
	public String getEmail() {
		return email;
	}


	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return "NONE";
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getClaims() {
		return null;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return null;
	}

	@Override
	public OidcIdToken getIdToken() {
		return null;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}

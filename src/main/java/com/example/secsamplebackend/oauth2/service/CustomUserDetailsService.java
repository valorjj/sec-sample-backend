package com.example.secsamplebackend.oauth2.service;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username(= 이메일) 로 가입된 회원이 있는지 조회
		User user = userRepository.findByEmail(username).orElseThrow(() -> new CustomApiException("해당 이메일로 가입된 회원이 존재하지 않습니다."));
		/*
		* 스프링 시큐리티의 인증, 인가 과정을 거치기 위해서 OAuth2User, OidcUser, UserDetails 를 통합한 객체에 맵핑시켜서 반환시킵니다.
		* CustomUserDetails 객체로 받습니다.
		* */


		return null;
	}
}

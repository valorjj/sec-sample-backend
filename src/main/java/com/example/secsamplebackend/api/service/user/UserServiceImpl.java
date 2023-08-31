package com.example.secsamplebackend.api.service.user;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.domain.user.dto.UserRequestDTO.UserSignupRequestDTO;
import com.example.secsamplebackend.api.domain.user.dto.UserResponseDTO.UserSignupResponseDTO;
import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public User findUserByUserSeq(Long userSeq) {
		// return userRepository.findByIdx(userSeq)
		// 		.orElseThrow(() -> new CustomApiException("해당 유저가 존재하지 않습니다."));
		return userRepository.findByIdx(userSeq)
				.orElse(null);
	}

	@Override
	@Transactional
	public User findUserByEmail(String email) {
		// return userRepository.findByEmail(email)
		// 		.orElseThrow(() -> new CustomApiException("해당 유저가 존재하지 않습니다."));
		return userRepository.findByEmail(email)
				.orElse(null);
	}

	@Override
	@Transactional
	public UserSignupResponseDTO signupUser(UserSignupRequestDTO userSignupRequestDTO) {
		// 1.
		Optional<User> userOP = userRepository.findByEmail(userSignupRequestDTO.getEmail());
		// 2.
		if (userOP.isPresent()) {
			throw new CustomApiException("이미 가입된 유저");
		}
		User userPS = userRepository.save(userSignupRequestDTO.to());
		return new UserSignupResponseDTO(userPS);
	}

}

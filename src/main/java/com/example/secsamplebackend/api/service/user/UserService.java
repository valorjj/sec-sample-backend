package com.example.secsamplebackend.api.service.user;

import com.example.secsamplebackend.api.domain.user.dto.UserRequestDTO.UserSignupRequestDTO;
import com.example.secsamplebackend.api.domain.user.dto.UserResponseDTO.UserSignupResponseDTO;
import com.example.secsamplebackend.api.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

	User findUserByUserSeq(Long userSeq);

	User findUserByEmail(String email);

	UserSignupResponseDTO signupUser(UserSignupRequestDTO userSignupRequestDTO);

}

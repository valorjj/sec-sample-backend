package com.example.secsamplebackend.api.controller.user;

import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.service.user.UserServiceImpl;
import com.example.secsamplebackend.oauth2.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
	private UserServiceImpl userServiceImpl;

	@GetMapping("/me")
	// @PreAuthorize("hasRole('USER')")
	public User getCurrentUser(
			@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		return userServiceImpl.findUserByUserSeq(currentUser.getIdx());
	}

}

package com.example.secsamplebackend.api.repository;

import com.example.secsamplebackend.api.domain.user.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByIdx(Long idx);

	@Override
	@NotNull
	List<User> findAll();

	Boolean existsByEmail(String email);

}

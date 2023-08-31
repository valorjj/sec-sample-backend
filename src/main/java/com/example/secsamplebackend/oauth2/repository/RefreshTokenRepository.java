package com.example.secsamplebackend.oauth2.repository;

import com.example.secsamplebackend.oauth2.domain.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	@Query("SELECT r.refreshToken FROM RefreshToken r WHERE r.user.idx = :idx")
	Optional<RefreshToken> getRefreshTokenByUserIdx(@Param("idx") Long idx);


}

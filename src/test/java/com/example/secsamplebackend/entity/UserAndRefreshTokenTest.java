package com.example.secsamplebackend.entity;

import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.oauth2.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


/**
 * {@code @DataJpaTest} 는 JPA 에 꼭 필요한 클래스만을 가져온다. <br>
 * {@code @AutoConfigureTestDatabase} 는 내장된 데이터베이스를 사용한다. {@code @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)} 을 통해 실제 데이터베이스에 테스트 가능하다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserAndRefreshTokenTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Test
	public void user_one_to_one_bidirectional() {
		// given


		// when

		// then

	}

}

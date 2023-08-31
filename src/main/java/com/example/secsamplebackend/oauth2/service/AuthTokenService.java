package com.example.secsamplebackend.oauth2.service;

import com.example.secsamplebackend.api.domain.user.dto.UserResponseDTO.OAuth2UserResponseDTO;
import com.example.secsamplebackend.api.domain.user.entity.User;
import com.example.secsamplebackend.api.repository.UserRepository;
import com.example.secsamplebackend.config.envs.JwtProperties;
import com.example.secsamplebackend.oauth2.domain.CustomUserDetails;
import com.example.secsamplebackend.oauth2.domain.entity.RefreshToken;
import com.example.secsamplebackend.oauth2.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jeongjin Kim
 * @fileName AuthTokenService
 * @since 2023.08.22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthTokenService {

	private final JwtProperties jwtProperties;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	/**
	 * @param authentication {@code Authentication}
	 * @return 새롭게 생성된 엑세스 토큰
	 */
	public String createAccessToken(Authentication authentication) {
		// 1. 인증된 사용자 정보를 CustomUserDetails 에 맵핑시킵니다.
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String userId = userDetails.getName();
		// 2. 인증된 사용자 정보에서 authorities 를 추출합니다.
		String role = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		log.debug("[+] role := [{}]", role);
		// 3. access_token 을 생성하고 리턴합니다.
		return Jwts.builder()
				.setHeader(createHeader())                          // 토큰 헤더
				.signWith(createSignature())                        // 토큰의 sign key
				.setSubject(userId)                                 // 토큰의 식별자로 사용되는 유저의 ID
				.claim(jwtProperties.getAuthoritiesKey(), role)     // 사용자의 권한
				.setIssuedAt(new Date())                            // 토큰이 생성된 현재 시간
				.setExpiration(createAccessTokenExpirationDate())   // 토큰의 만료시간
				.compact();                                         // 생성된 JWT 를 String 값으로 변환
	}

	/**
	 * 인증된 유저 정보를 바탕으로 refresh_token 을 생성합니다.
	 *
	 * @param authentication {@code Authentication} 인증된 사용자 정보
	 * @param response       http-response
	 * @return
	 */
	public void createRefreshToken(Authentication authentication, HttpServletResponse response) {
		// 1. refresh_token 을 생성합니다.
		String refreshToken = Jwts.builder()
				.setHeader(createHeader())                          // 토큰 헤더
				.signWith(createSignature())                        // 토큰 sign key
				.setIssuer("Jeongjin Kim")                          // 발급자 정보
				.setIssuedAt(new Date())                            // 토큰 생성 시간
				.setExpiration(createRefreshTokenExpirationDate())  // 토큰의 만료시간
				.compact();                                         // 생성된 JWT 를 String 값으로 변환
		// 2. refresh_token 을 DB 에 저장합니다.
		saveRefreshToken(authentication, refreshToken);
		// 3. 리프레시 토큰은 HTTP only 로 쿠키에 저장합니다.
		ResponseCookie cookie = ResponseCookie.from(jwtProperties.getCookieRefreshTokenKey(), refreshToken)
				.httpOnly(true)
				.secure(true)
				.sameSite("Lax")
				.maxAge(jwtProperties.getRefreshToken().getExpiration() / 1000)
				.path("/")
				.build();
		// 4. response 의 헤더에 refresh_token 을 cookie 로 저장합니다.
		response.addHeader("Set-Cookie", cookie.toString());
	}

	/**
	 * 생성한 refresh_token 을 DB 에 저장합니다.
	 *
	 * @param authentication
	 * @param refreshToken
	 */
	public void saveRefreshToken(Authentication authentication, @NotNull String refreshToken) {
		// 1. 인증된 사용자 정보를 CustomUserDetails 에 맵핑시킵니다.
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Long userIdx = userDetails.getIdx();
		Optional<User> userOptional = userRepository.findByIdx(userIdx);

		userOptional.ifPresent(user -> {
			RefreshToken createdRefreshToken = RefreshToken.builder()
					.user(user)
					.refreshToken(refreshToken)
					.build();
			// 2. DB 에 저장된 유저의 식별값, refresh_token 을 DB 에 저장합니다.
			refreshTokenRepository.save(createdRefreshToken);
		});
	}

	/**
	 * 전달받은 access_token 으로 Authentication 객체를 생성합니다.
	 *
	 * @param accessToken 클라이언트가 전달한 access_token
	 * @return {@code Authentication}
	 */
	public Authentication getAuthentication(@NotNull String accessToken) {
		// 1.
		Claims parsedClaims = parseClaims(accessToken);
		String[] authoritiesArray = parsedClaims.get(jwtProperties.getAuthoritiesKey()).toString().split(",");
		// 2.
		List<SimpleGrantedAuthority> authorities = Arrays.stream(authoritiesArray).map(SimpleGrantedAuthority::new).toList();
		// 3.
		CustomUserDetails principal = new CustomUserDetails(Long.valueOf(parsedClaims.getSubject()), "", "", authorities);
		// 4.
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	/**
	 * sign_key 를 사용해서 token 의 claims 을 추출합니다.
	 *
	 * @param accessToken 클라이언트가 전달한 access_token
	 * @return {@code Claims}
	 */
	private Claims parseClaims(@NotNull String accessToken) {
		return Jwts.parserBuilder()
				.setSigningKey(createSignature()).build()
				.parseClaimsJws(accessToken)
				.getBody();
	}

	/**
	 * access_token 의 유효성 여부를 검증합니다.
	 *
	 * @param accessToken client 가 전달한 access_token
	 * @return 유효 여부 true/false
	 */
	public Boolean validateAuthToken(@NotNull String accessToken) {
		try {
			Jwts.parserBuilder().setSigningKey(createSignature()).build().parseClaimsJws(accessToken);
			return true;
		} catch (ExpiredJwtException e) {
			log.info("[+] 만료된 토큰입니다. := [{}]", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.info("[+] 유효하지 않은 JWT 토큰입니다. := [{}]", e.getMessage());
		} catch (JwtException e) {
			log.info("[+] JWT 에러가 발생했습니다. := [{}]", e.getMessage());
		}
		return false;
	}


	/**
	 * config-jwt.yml 에 작성한 secret_key 를 Base64 로 HS512 알고리즘을 사용해 암호화 합니다.
	 * 암호화된 값을 사용해서 JWT 서명(Signature) 발급합니다.
	 * TODO: RSA 방식으로 변경
	 *
	 * @return
	 */
	private Key createSignature() {
		return new SecretKeySpec(
				Base64.encodeBase64(jwtProperties.getJwtSecretKey().getBytes()),
				SignatureAlgorithm.HS512.getJcaName()
		);
	}

	/**
	 * JWT 토큰의 Header 값을 생성합니다.
	 *
	 * @return
	 */
	private Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS512");
		header.put("regDate", System.currentTimeMillis());
		return header;
	}

	/**
	 * 토큰에서 Header 값을 추출합니다.
	 *
	 * @param header
	 * @return
	 */
	public String getTokenFromHeader(@NotNull String header) {
		return header.split(" ")[1];
	}


	/**
	 * access_token 생성 시 사용되는 만료시간을 리턴합니다.
	 *
	 * @return {@code Date} access_token 의 만료시간
	 */
	private Date createAccessTokenExpirationDate() {
		Date now = new Date();
		return new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration());
	}

	/**
	 * refres_token 생성 시 사용되는 만료시간을 리턴합니다.
	 *
	 * @return {@code Date} refresh_token 의 만료시간
	 */
	private Date createRefreshTokenExpirationDate() {
		Date now = new Date();
		return new Date(now.getTime() + jwtProperties.getRefreshToken().getExpiration());
	}


	// ---------------------------------------------- //
	// -------------------- 절취선 -------------------- //
	// ---------------------------------------------- //

	/**
	 * 인가서버에서 전송한 사용자 정보 기반으로 token 을 생성
	 * 문제점: access 토큰, refresh 구분이 없는데요?
	 *
	 * @param oAuth2UserResponseDTO 인가서버에서 받아온 사용자 정보
	 * @return String : 사용자정보 기반으로 생성된 JWT 토큰
	 */
	public String generateJwtToken(OAuth2UserResponseDTO oAuth2UserResponseDTO) {
		return Jwts.builder()
				.setHeader(createHeader())
				.setClaims(createClaims(oAuth2UserResponseDTO))
				.setSubject(oAuth2UserResponseDTO.getUserId())
				.signWith(createSignature())
				.setExpiration(createAccessTokenExpirationDate())
				.compact();
	}

	/**
	 * 토큰의 유효성 검사
	 *
	 * @param token JWT 토큰
	 * @return Boolean : JWT 토큰의 유효성 여부 true/false 반환
	 */
	public Boolean isValidAuthToken(@NotNull String token) {
		try {
			Claims claimsFromToken = getClaimsFromToken(token);

			assert claimsFromToken != null;
			log.info("[+] expiration time := {}", claimsFromToken.getExpiration());
			log.info("[+] user id := {}", claimsFromToken.get("userId"));
			log.info("[+] username := {}", claimsFromToken.get("username"));

			return true;
		} catch (ExpiredJwtException e) {
			log.error("[x] 토큰의 유효시간이 만료되었습니다. := {}", e.getMessage());
			return false;
		} catch (JwtException e) {
			log.error("[x] 토큰이 변조되었을 가능성이 있습니다. := {}", e.getMessage());
			return false;
		} catch (NullPointerException e) {
			log.error("[x] 토큰이 null 입니다. := {}", e.getMessage());
			return false;
		}
	}


	private Map<String, Object> createClaims(@NotNull OAuth2UserResponseDTO oAuth2UserResponseDTO) {
		Map<String, Object> claims = new HashMap<>();

		log.info("[+] userId := {}", oAuth2UserResponseDTO.getUserId());
		log.info("[+] username := {}", oAuth2UserResponseDTO.getUsername());
		log.info("[+] email := {}", oAuth2UserResponseDTO.getEmail());

		claims.put("userId", oAuth2UserResponseDTO.getUserId());
		claims.put("username", oAuth2UserResponseDTO.getUsername());
		return claims;
	}

	private Claims getClaimsFromToken(@NotNull String token) {
		return Jwts.parserBuilder().setSigningKey(createSignature())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String getUserIdFromToken(@NotNull String token) {
		Claims claims = getClaimsFromToken(token);
		return (String) claims.get("userId");
	}


}

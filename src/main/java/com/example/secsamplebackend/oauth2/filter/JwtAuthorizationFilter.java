package com.example.secsamplebackend.oauth2.filter;

import com.example.secsamplebackend.api.common.exception.custom.*;
import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import com.example.secsamplebackend.oauth2.contant.AuthTokenConstant;
import com.example.secsamplebackend.util.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author Jeongjin Kim
 * @fileName JwtAuthorizationFilter
 * @since 2023.08.23
 */
@RequiredArgsConstructor
// @Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final AuthTokenService authTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		// AuthToken 이 필요하지 않은 API
		List<String> list = Arrays.asList(
				"/api/v1/user/login",
				"/api/v1/index"
		);

		// AuthToken 이 필요하지 않은 API 인 경우 해당 필터를 거치지 않음
		if (list.contains(request.getRequestURI())) {
			filterChain.doFilter(request, response);
		}

		// options 요청일 경우 해당 필터를 거치지 않음
		if (request.getMethod().equalsIgnoreCase("options")) {
			filterChain.doFilter(request, response);
		}

		// Client 에서 API 를 요청할 때 Header 를 확인
		String header = request.getHeader(AuthTokenConstant.AUTH_HEADER);
		log.debug("[+] authorization header := {}", header);

		try {
			// Header 에 authorization code 가 존재하는 경우 분기점
			if (header != null && !header.equalsIgnoreCase("")) {
				// Header 에 존재하는 (Authorization) 토큰을 추출
				String tokenFromHeader = authTokenService.getTokenFromHeader(header);
				// 인가 토큰의 유효성 검사
				if (authTokenService.isValidAuthToken(tokenFromHeader)) {
					// 사용자의 아이디 추출
					String userIdFromToken = authTokenService.getUserIdFromToken(tokenFromHeader);
					log.debug("[+] user id: {}", userIdFromToken);
					CustomAssert.notEmptyString(userIdFromToken, "토큰에 사용자 아이디가 존재하지 않습니다.", CustomNoArgsException.class);
					// 해당 유저의 아이디가 존재하는지 체크
					if (StringUtils.checkStringIsNullOfEmpty(userIdFromToken)) {
						filterChain.doFilter(request, response);
					}
				} else {
					throw new CustomApiException("유효한 토큰이 아닙니다.");
				}
			}
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter printWriter = response.getWriter();
			JSONObject jsonObject = jsonResponseWrapper(e);

			printWriter.println(jsonObject);
			printWriter.flush();
			printWriter.close();
		}
	}

	/**
	 * Token 관련 Exception 발생 시, 응답 값을 구성
	 *
	 * @param e {@code Exception}
	 * @return {@code JSONObject}
	 */
	private JSONObject jsonResponseWrapper(Exception e) {
		String resultMessage;
		// JWT 토큰 만료
		if (e instanceof ExpiredJwtException) {
			resultMessage = "TOKEN Expired";
		}
		// JWT 토큰내에서 오류 발생 시
		else if (e instanceof JwtException) {
			resultMessage = "TOKEN Parsing JwtException";
		}
		// 이외 JTW 토큰내에서 오류 발생
		else {
			resultMessage = "OTHER TOKEN ERROR";
		}

		HashMap<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("status", 401);
		jsonMap.put("code", "9999");
		jsonMap.put("message", resultMessage);
		jsonMap.put("reason", e.getMessage());

		JSONObject jsonObject = new JSONObject(jsonMap);
		logger.error("[x] 토큰 관련 에러 발생 := {}", e);

		return jsonObject;
	}
}

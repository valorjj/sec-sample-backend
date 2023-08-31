package com.example.secsamplebackend.api.common.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Jeongjin Kim
 * @fileName ErrorCode
 * @since 2023.08.23
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {

	// 서버가 요청을 처리할 수 없는 경우
	BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),
	// 서버가 유효하지 않은 데이터를 받은 경우
	REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),
	// 타입이 일치하지 않는 경우
	INVALID_TYPE_VALUE(400, "G003", "Invalid Type Value"),
	// Request Parameter 로 데이터가 전달되지 않을 경우
	MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"),
	// 입력, 출력에서 문제가 발생한 경우
	IO_ERROR(400, "G008", "I/O Exception"),
	// com.google.gson JSON 파싱 실패
	JSON_PARSE_ERROR(400, "G009", "JsonParseException"),
	// com.fasterxml.jackson.core Processing Error
	JACKSON_PROCESS_ERROR(400, "G010", "com.fasterxml.jackson.core Exception"),
	// 요청자의 권한이 부족한 경우
	FORBIDDEN_ERROR(403, "G004", "Forbidden Exception"),
	// 서버로 요청한 리소스가 존재하지 않는 경우
	NOT_FOUND_ERROR(404, "G005", "Not Found Exception"),
	// NULL 값이 입력되지 않아야 하는 곳에 NULL 값이 전달된 경우
	NULL_POINT_ERROR(404, "G006", "Null Point Exception"),
	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_ERROR(404, "G007", "handle Validation Exception"),
	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_HEADER_ERROR(404, "G007", "Header에 데이터가 존재하지 않는 경우 "),
	// 서버가 처리 할 방법을 모르는 경우
	INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),
	// 인증을 받지 않는 경우
	AUTH_IS_NULL(200, "A404", "AUTH_IS_NULL"),
	// 토큰을 통한 인증에 실패한 경우
	AUTH_TOKEN_FAIL(200, "A405", "AUTH_TOKEN_FAIL"),
	// 토큰이 유효하지 않은 경우
	AUTH_TOKEN_INVALID(200, "A406", "AUTH_TOKEN_INVALID"),
	// 토큰이 일치하지 않는 경우
	AUTH_TOKEN_NOT_MATCH(200, "A407", "AUTH_TOKEN_NOT_MATCH"),
	// 토큰이 NULL 인 경우
	AUTH_TOKEN_IS_NULL(200, "A408", "AUTH_TOKEN_IS_NULL"),

	// Business Exception Error
	BUSINESS_EXCEPTION_ERROR(200, "B999", "Business Exception Error"),

	// Transaction Insert Error
	INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),

	// Transaction Update Error
	UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),

	// Transaction Delete Error
	DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),

	;

	// 에러 코드의 '코드 상태'을 반환
	private Integer status;

	// 에러 코드의 '코드간 구분 값'을 반환
	private String code;

	// 에러 코드의 '코드 메시지'을 반환
	private String message;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}

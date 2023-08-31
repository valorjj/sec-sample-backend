package com.example.secsamplebackend.api.common.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Jeongjin Kim
 * @fileName ErrorResponse
 * @since 2023.08.23
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private Integer status;             // 에러 상태 코드
	private String code;                // 에러 구분 코드
	private String message;             // 에러 메시지
	private List<FieldError> errors;    // 상태 에러 메시지
	private String reason;              // 에러 발생 원인


	/**
	 * [1] ErrorResponse 생성자
	 *
	 * @param errorCode {@code ErrorCode}
	 */
	@Builder
	protected ErrorResponse(final ErrorCode errorCode) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.errors = new ArrayList<>();
	}

	/**
	 * [2] ErrorResponse 생성자
	 *
	 * @param errorCode {@code ErrorCode}
	 * @param reason    에러 발생 원인
	 */
	@Builder
	protected ErrorResponse(final ErrorCode errorCode, final String reason) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.reason = reason;
	}

	/**
	 * [3] ErrorResponse 생성자
	 *
	 * @param errorCode {@code ErrorCode}
	 * @param errors    에러 리스트
	 */
	@Builder
	protected ErrorResponse(final ErrorCode errorCode, List<FieldError> errors) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.errors = errors;
	}

	/**
	 * [1]
	 *
	 * @param errorCode {@code ErrorCode}
	 * @return {@code ErrorResponse}
	 */
	public static ErrorResponse of(final ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}

	/**
	 * [2]
	 *
	 * @param errorCode     {@code ErrorCode}
	 * @param bindingResult {@code BindingResult}
	 * @return {@code ErrorResponse}
	 */
	public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bindingResult) {
		return new ErrorResponse(errorCode, FieldError.of(bindingResult));
	}

	/**
	 * [3]
	 *
	 * @param errorCode {@code ErrorCode}
	 * @param reason    에러가 발생 이유
	 * @return {@code ErrorResponse}
	 */
	public static ErrorResponse of(final ErrorCode errorCode, final String reason) {
		return new ErrorResponse(errorCode, reason);
	}


	public record FieldError(String field, String value, String reason) {
		public static List<FieldError> of(final String field, final String value, final String reason) {
			List<FieldError> fieldErrors = new ArrayList<>();
			fieldErrors.add(new FieldError(field, value, reason));
			return fieldErrors;
		}

		private static List<FieldError> of(final BindingResult bindingResult) {
			final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
			return fieldErrors.stream()
					.map(error -> new FieldError(
							error.getField(),
							error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
							error.getDefaultMessage()))
					.collect(Collectors.toList());
		}
	}


}

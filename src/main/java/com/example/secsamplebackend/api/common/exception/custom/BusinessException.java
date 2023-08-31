package com.example.secsamplebackend.api.common.exception.custom;

import com.example.secsamplebackend.api.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	@Builder
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	@Builder
	public BusinessException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}

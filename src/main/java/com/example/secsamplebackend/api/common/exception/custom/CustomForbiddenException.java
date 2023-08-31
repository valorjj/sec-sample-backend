package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class CustomForbiddenException extends CustomAbstractException {

	@Serial
	private static final long serialVersionUID = 1L;

	public CustomForbiddenException() {
		super();
	}

	public CustomForbiddenException(String errorMessage) {
		super(errorMessage);
	}

	public CustomForbiddenException(Throwable throwable) {
		super(throwable);
	}

	public CustomForbiddenException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.FORBIDDEN;
	}
}

package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class CustomUnauthorizedException extends CustomAbstractException {

	@Serial
	private static final long serialVersionUID = 1L;

	public CustomUnauthorizedException() {
		super();
	}

	public CustomUnauthorizedException(String errorMessage) {
		super(errorMessage);
	}

	public CustomUnauthorizedException(Throwable throwable) {
		super(throwable);
	}

	public CustomUnauthorizedException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.UNAUTHORIZED;
	}
}

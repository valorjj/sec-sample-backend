package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class CustomApiException extends CustomAbstractException {

	@Serial
	private static final long serialVersionUID = 1L;

	public CustomApiException() {
		super();
	}

	public CustomApiException(String errorMessage) {
		super(errorMessage);
	}

	public CustomApiException(Throwable throwable) {
		super(throwable);
	}

	public CustomApiException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}

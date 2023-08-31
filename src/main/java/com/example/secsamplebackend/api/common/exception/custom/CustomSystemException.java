package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

public class CustomSystemException extends CustomAbstractException {

	public CustomSystemException() {
		super();
	}

	public CustomSystemException(String errorMessage) {
		super(errorMessage);
	}

	public CustomSystemException(Throwable throwable) {
		super(throwable);
	}

	public CustomSystemException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}

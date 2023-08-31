package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

public class CustomNotFoundException extends CustomAbstractException {


	public CustomNotFoundException() {
		super();
	}

	public CustomNotFoundException(String errorMessage) {
		super(errorMessage);
	}

	public CustomNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public CustomNotFoundException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}
}

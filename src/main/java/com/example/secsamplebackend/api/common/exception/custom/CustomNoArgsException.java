package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

public class CustomNoArgsException extends CustomAbstractException{

	public CustomNoArgsException() {
		super();
	}

	public CustomNoArgsException(String errorMessage) {
		super(errorMessage);
	}

	public CustomNoArgsException(Throwable throwable) {
		super(throwable);
	}

	public CustomNoArgsException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}

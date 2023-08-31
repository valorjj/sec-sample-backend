package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public abstract class CustomAbstractException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public CustomAbstractException() {
		super();
	}

	public CustomAbstractException(String errorMessage) {
		super(errorMessage);
	}

	public CustomAbstractException(Throwable throwable) {
		super(throwable);
	}

	public CustomAbstractException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	public abstract HttpStatus getHttpStatus();

}

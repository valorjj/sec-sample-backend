package com.example.secsamplebackend.api.common.exception;

import com.example.secsamplebackend.api.common.dto.ResponseDTO;
import com.example.secsamplebackend.api.common.exception.custom.BusinessException;
import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import com.example.secsamplebackend.api.common.exception.custom.CustomUnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomApiException.class)
	public ResponseEntity<?> customApiException(CustomApiException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(-1, e.getMessage(), null));
	}

	@ExceptionHandler(CustomUnauthorizedException.class)
	public ResponseEntity<?> customSecurityException(CustomUnauthorizedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(-1, e.getMessage(), null));
	}

	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
		log.error("handleNullPointerException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BUSINESS_EXCEPTION_ERROR, e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}


}

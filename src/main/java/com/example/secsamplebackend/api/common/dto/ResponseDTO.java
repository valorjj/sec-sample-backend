package com.example.secsamplebackend.api.common.dto;

public record ResponseDTO<T>(Integer code, String message, T data) {

}

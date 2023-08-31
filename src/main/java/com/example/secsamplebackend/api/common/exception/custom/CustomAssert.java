package com.example.secsamplebackend.api.common.exception.custom;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;

/**
 * <a href="https://eblo.tistory.com/63">참고</a>
 */
public class CustomAssert extends Assert {
	private static final String NUMBER_ALPHABET_PATTERN = "^[a-zA-Z\\d]+$";
	private static final String NUMERIC_PATTERN = "^[\\+\\-]{0,1}[\\d]+$";
	private static final String FLOAT_PATTERN = "^[\\+\\-]{0,1}[\\d]+[\\.][0-9]+$";

	public static void notNull(@Nullable Object object, @Nullable String message, final Class<? extends CustomAbstractException> exceptionClass) {
		if (object == null) {
			throwException(message, exceptionClass);
		}
	}


	public static void notEmptyString(@Nullable String str, @Nullable String message, final Class<? extends CustomAbstractException> exceptionClass) {
		if (str == null || str.trim().equalsIgnoreCase("")) {
			throwException(message, exceptionClass);
		}
	}

	public static void notEmpty(@Nullable Object[] array, @Nullable String message, final Class<? extends CustomAbstractException> exceptionClass) {
		if (ObjectUtils.isEmpty(array)) {
			throwException(message, exceptionClass);
		}
	}


	public static void notEmpty(@Nullable Collection<?> collection, String message, final Class<? extends CustomAbstractException> exceptionClass) {
		if (CollectionUtils.isEmpty(collection)) {
			throwException(message, exceptionClass);
		}
	}

	public static void notEmpty(@Nullable Map<?, ?> map, String message, final Class<? extends CustomAbstractException> exceptionClass) {
		if (CollectionUtils.isEmpty(map)) {
			throwException(message, exceptionClass);
		}
	}

	private static void throwException(String message, final Class<? extends CustomAbstractException> exceptionClass) {
		try {
			throw exceptionClass.getDeclaredConstructor(String.class).newInstance(message);
		} catch (Exception e) {
			e.fillInStackTrace();
			throw new CustomSystemException("예외 처리 중 오류가 발생했습니다. " + e.getMessage());
		}
	}

}

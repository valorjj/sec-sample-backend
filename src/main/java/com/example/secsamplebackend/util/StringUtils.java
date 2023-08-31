package com.example.secsamplebackend.util;

public class StringUtils {

	public static Boolean checkStringIsNullOfEmpty(String str) {
		return str != null && !str.equalsIgnoreCase("");
	}
}

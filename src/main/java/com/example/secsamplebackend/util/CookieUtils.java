package com.example.secsamplebackend.util;

import com.example.secsamplebackend.api.common.exception.custom.CustomApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class CookieUtils {

	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				log.debug("[*] cookie_name := [{}], cookie_value := [{}]", cookie.getName(), cookie.getValue());
				if (cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				log.debug("[*] cookie_name := [{}], cookie_value := [{}]", cookie.getName(), cookie.getValue());
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static String serialize(Object object) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
	}

	// public static <T> T deserialize(Cookie cookie, Class<T> targetClass) {
	// 	return targetClass.cast(SerializationUtils.deserialize(
	// 			Base64.getUrlDecoder().decode(cookie.getValue())));
	// }

	public static <T> T deserialize(Cookie cookie, Class<T> targetClass) {
		byte[] decodedToken = Base64.getUrlDecoder().decode(cookie.getValue());
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decodedToken));
			Object object = ois.readObject();
			return targetClass.cast(object);
		} catch (ClassNotFoundException | IOException e) {
			log.error("error := {}", e.getMessage());
			throw new CustomApiException(e);
		}
	}

}

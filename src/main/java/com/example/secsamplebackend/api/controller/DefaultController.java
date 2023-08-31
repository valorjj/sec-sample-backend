package com.example.secsamplebackend.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class DefaultController {

	@GetMapping("/indexPage")
	public String index() {
		return "Hello World!";
	}

	// @GetMapping("/oauth2/authorization/google")
	// public String google(@RequestParam(value="redirect_url") String redirectUrl) {
	// 	log.info("redirectUrl: {}", redirectUrl);
	// 	return "hi";
	// }
}

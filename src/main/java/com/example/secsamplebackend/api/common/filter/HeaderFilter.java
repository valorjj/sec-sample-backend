package com.example.secsamplebackend.api.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HeaderFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		res.setHeader("Access-Control-Max-Age", "3600");
		res.setHeader(
				"Access-Control-Allow-Headers",
				"X-Requested-With, Content-Type, Authorization, X-XSRF-token"
		);
		res.setHeader("Access-Control-Allow-Credentials", "false");

		chain.doFilter(request, response);
	}
}

package com.example.secsamplebackend.config.security;


import com.example.secsamplebackend.oauth2.filter.JwtAuthenticationFilter;
import com.example.secsamplebackend.oauth2.filter.JwtAuthorizationFilter;
import com.example.secsamplebackend.oauth2.exception.JwtAccessDeniedHandler;
import com.example.secsamplebackend.oauth2.exception.JwtAuthenticationEntryPoint;
import com.example.secsamplebackend.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.example.secsamplebackend.oauth2.handler.OAuth2AuthenticationSuccessfulHandler;
import com.example.secsamplebackend.oauth2.repository.CookieAuthorizationRequestRepository;
import com.example.secsamplebackend.oauth2.service.AuthTokenService;
import com.example.secsamplebackend.oauth2.service.CustomOAuth2UserService;
import com.example.secsamplebackend.oauth2.service.CustomOidcUserService;
import com.example.secsamplebackend.oauth2.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher.Builder;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true
)
public class WebSecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOidcUserService customOidcUserService;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final AuthTokenService authTokenService;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, Builder mvc) throws Exception {
		// 1. CSRF disabled
		http.csrf(AbstractHttpConfigurer::disable);

		// 2. CORS
		// http.cors(configurer -> configurer.configurationSource(corsConfig.corsConfigurationSource()));

		// 3. Session disabled
		http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 4. URL filter
		http.authorizeHttpRequests(registry -> registry
				.requestMatchers(
						mvc.pattern("/api/v1/user/**"),
						mvc.pattern("/api/v1/token/**"),
						mvc.pattern("/h2-console/**")
				)
				.permitAll()
				.anyRequest().authenticated()
		);

		// 5. OAuth 2.0
		http.oauth2Login(configurer -> configurer
						.authorizationEndpoint(config -> config
										.authorizationRequestRepository(cookieAuthorizationRequestRepository())
								// .baseUri("/oauth2/authorization")
						)
						.userInfoEndpoint(config -> config
								.userService(customOAuth2UserService)
								.oidcUserService(customOidcUserService)
						)
						.redirectionEndpoint(config -> config
								.baseUri("/login/oauth2/code/")
						)
						.successHandler(oAuth2AuthenticationSuccessfulHandler())
						.failureHandler(oAuth2AuthenticationFailureHandler())
				// .loginPage("/login")
		);


		// 7. Handle Authentication & Authorization Exceptions
		http.exceptionHandling(config -> config
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
		);

		// 9. Form Login
		http.formLogin(Customizer.withDefaults());

		// 8. Handle Logout Request

		// 9. Http Basic / disabled
		http.httpBasic(AbstractHttpConfigurer::disable);



		// 6. Register Custom Filters
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


		return http.build();
	}


	@Scope("prototype")
	@Bean
	public Builder mvc(HandlerMappingIntrospector introspector) {
		return new Builder(introspector);
	}

	@Bean
	public CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new CookieAuthorizationRequestRepository();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(authTokenService);
	}

	@Bean
	public OAuth2AuthenticationSuccessfulHandler oAuth2AuthenticationSuccessfulHandler() {
		return new OAuth2AuthenticationSuccessfulHandler(jwtService);
	}
	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(cookieAuthorizationRequestRepository());
	}

}

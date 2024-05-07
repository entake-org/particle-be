package io.sdsolutions.particle.security.config;

import io.sdsolutions.particle.security.constants.AntMatchers;
import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security Configuration - ensures that all APIs exposed at /api/* are protected. The spring security filter
 * chain can be modified upon extending this abstract class. This will allow for the introduction of more nuanced
 * authorization.
 * 
 * @author Nick DiMola
 * 
 */
public class SecurityConfiguration {

	private final UserDetailsService userDetailsService;

	private final CorsConfigurationProperties corsConfigurationProperties;

	public SecurityConfiguration(UserDetailsService userDetailsService, CorsConfigurationProperties corsConfigurationProperties) {
		this.userDetailsService = userDetailsService;
		this.corsConfigurationProperties = corsConfigurationProperties;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedOrigins(corsConfigurationProperties.getOrigin());
		corsConfig.setAllowedMethods(corsConfigurationProperties.getMethods());
		corsConfig.setAllowedHeaders(corsConfigurationProperties.getAllowheaders());
		corsConfig.setExposedHeaders(corsConfigurationProperties.getExposeheaders());
		corsConfig.setMaxAge(corsConfigurationProperties.getMaxage());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, AutoLoginFilter autoLoginFilter) throws Exception {
		http.authenticationProvider(authenticationProvider).authorizeHttpRequests(
		authorize -> authorize
				.requestMatchers(
					AntMatchers.PERMITTED_URLS
				).permitAll()
				.requestMatchers(
					AntMatchers.AUTHENTICATED_URLS
				).fullyAuthenticated()
		);

		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.csrf(AbstractHttpConfigurer::disable);

		http.cors(Customizer.withDefaults());

		http.addFilterBefore(autoLoginFilter, AuthorizationFilter.class);

		configureFilterChain(http);

		return http.getOrBuild();
	}

	protected void configureFilterChain(HttpSecurity http) {
		// Add other filters here
	}

	protected String getApiPath() {
		return "/api/**";
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper = new UserDetailsByNameServiceWrapper<>();
		wrapper.setUserDetailsService(userDetailsService);

		PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(wrapper);

		return provider;
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.and()
				.build();
	}

}

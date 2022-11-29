package io.sdsolutions.particle.security.config.impl;

import io.sdsolutions.particle.security.config.CorsConfigurationProperties;
import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import io.sdsolutions.particle.security.config.SecurityConfiguration;
import io.sdsolutions.particle.security.filter.impl.PassThroughSecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This configuration should NEVER be used for a production application. This is specifically for local development
 * to eliminate the annoyances of dealing with security. Upon commit, a real security implementation must be extended
 * so that users are properly identified by the application.
 *
 * @author ndimola
 *
 */
public class PassThroughSecurityConfiguration extends SecurityConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassThroughSecurityConfiguration.class);


	public PassThroughSecurityConfiguration(
			UserDetailsService userDetailsService,
			CorsConfigurationProperties corsConfigurationProperties
	) {
		super(userDetailsService, corsConfigurationProperties);
	}

	@Bean
	public AutoLoginFilter autoLoginFilter(AuthenticationManager authenticationManager) {
		return new PassThroughSecurityFilter(authenticationManager);
	}
}

package io.entake.particle.security.config.impl;

import io.entake.particle.security.config.CorsConfigurationProperties;
import io.entake.particle.security.filter.AutoLoginFilter;
import io.entake.particle.security.config.SecurityConfiguration;
import io.entake.particle.security.filter.impl.PassThroughSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * This configuration should NEVER be used for a production application. This is specifically for local development
 * to eliminate the annoyances of dealing with security. Upon commit, a real security implementation must be extended
 * so that users are properly identified by the application.
 *
 * @author ndimola
 *
 */
public class PassThroughSecurityConfiguration extends SecurityConfiguration {

	public PassThroughSecurityConfiguration(
            Environment environment,
			CorsConfigurationProperties corsConfigurationProperties
	) {
		super(environment, corsConfigurationProperties);
	}

	@Bean
	public AutoLoginFilter autoLoginFilter(AuthenticationManager authenticationManager) {
		return new PassThroughSecurityFilter(authenticationManager);
	}
}

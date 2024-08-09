package io.entake.particle.security.config;

import io.entake.particle.security.constants.AntMatchers;
import io.entake.particle.security.filter.AutoLoginFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;

/**
 * Spring Security Configuration - ensures that all APIs exposed at /api/* are protected. The spring security filter
 * chain can be modified upon extending this abstract class. This will allow for the introduction of more nuanced
 * authorization.
 * 
 * @author Nick DiMola
 * 
 */
public class SecurityConfiguration {

    public static final String PUBLIC_URLS_PROPERTY = "security.urls.public";
    public static final String AUTHENTICATED_URLS_PROPERTY = "security.urls.authenticated";

    protected final Environment environment;
	protected final CorsConfigurationProperties corsConfigurationProperties;

    private final UserDetailsService userDetailsService;

	public SecurityConfiguration(Environment environment, CorsConfigurationProperties corsConfigurationProperties) {
        this.environment = environment;
		this.corsConfigurationProperties = corsConfigurationProperties;

        this.userDetailsService = username -> new User(username, "", new ArrayList<>());
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
					getPublicUrlPatterns()
				).permitAll()
                .requestMatchers(
                    HttpMethod.OPTIONS, "/**"
                ).permitAll()
				.requestMatchers(
					getAuthenticatedUrlPatterns()
				).fullyAuthenticated()
		);

		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.csrf(AbstractHttpConfigurer::disable);

		http.cors(Customizer.withDefaults());

		http.addFilterAfter(autoLoginFilter, SecurityContextHolderFilter.class);

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
                .and().build();
	}

    protected String[] getPublicUrlPatterns() {
        String urlList = environment.getProperty(PUBLIC_URLS_PROPERTY);
        if (StringUtils.isNotBlank(urlList)) {
            return urlList.split(",");
        }

        return AntMatchers.PERMITTED_URLS;
    }

    protected String[] getAuthenticatedUrlPatterns() {
        String urlList = environment.getProperty(AUTHENTICATED_URLS_PROPERTY);
        if (StringUtils.isNotBlank(urlList)) {
            return urlList.split(",");
        }

        return AntMatchers.AUTHENTICATED_URLS;
    }

}

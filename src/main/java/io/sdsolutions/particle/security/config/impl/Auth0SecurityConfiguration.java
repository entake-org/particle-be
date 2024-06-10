package io.sdsolutions.particle.security.config.impl;

import io.sdsolutions.particle.security.config.CorsConfigurationProperties;
import io.sdsolutions.particle.security.constants.AntMatchers;
import io.sdsolutions.particle.security.services.SecurityService;
import io.sdsolutions.particle.security.services.impl.SecurityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

public class Auth0SecurityConfiguration {

    @Autowired
    private CorsConfigurationProperties corsConfigurationProperties;

    private final Environment environment;

    public Auth0SecurityConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityService securityService() {
        return new SecurityServiceImpl();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder =
                JwtDecoders.fromOidcIssuerLocation(environment.getRequiredProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri"));

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(environment.getRequiredProperty("auth0.audience"));
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(environment.getRequiredProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri"));
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers(
                        AntMatchers.PERMITTED_URLS
                )
                .permitAll()
                .requestMatchers(AntMatchers.AUTHENTICATED_URLS)
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();

        http.csrf().disable();

        http.cors();

        return http.getOrBuild();
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


    private static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;

        AudienceValidator(String audience) {
            this.audience = audience;
        }

        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

            if (jwt.getAudience().contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}

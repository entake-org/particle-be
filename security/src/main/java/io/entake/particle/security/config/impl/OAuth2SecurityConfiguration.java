package io.entake.particle.security.config.impl;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.entake.particle.security.config.CorsConfigurationProperties;
import io.entake.particle.security.filter.AutoLoginFilter;
import io.entake.particle.security.filter.impl.OAuth2AutoLoginFilter;
import io.entake.particle.security.config.SecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

public class OAuth2SecurityConfiguration extends SecurityConfiguration {

    public OAuth2SecurityConfiguration(
            Environment environment,
            CorsConfigurationProperties corsConfigurationProperties

    ) {
        super(environment, corsConfigurationProperties);
    }

    @Bean
    public ConfigurableJWTProcessor<SimpleSecurityContext> configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(
                environment.getRequiredProperty("security.oauth2.timeout.connection", Integer.class),
                environment.getRequiredProperty("security.oauth2.timeout.read", Integer.class));

        URL jwkSetURL = new URL(environment.getRequiredProperty("security.oauth2.identity_pool_url") +
                        environment.getRequiredProperty("security.oauth2.jwks_suffix"));

        JWKSource<SimpleSecurityContext> keySource = new RemoteJWKSet<>(jwkSetURL, resourceRetriever);
        ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SimpleSecurityContext> keySelector = new JWSVerificationKeySelector<>(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        return jwtProcessor;
    }

    @Bean
    public AutoLoginFilter autoLoginFilter(
            AuthenticationManager authenticationManager,
            ConfigurableJWTProcessor<SimpleSecurityContext> configurableJWTProcessor
    ) {
        return new OAuth2AutoLoginFilter(authenticationManager, configurableJWTProcessor, environment);
    }

}

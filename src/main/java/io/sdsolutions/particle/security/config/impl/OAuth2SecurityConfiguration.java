package io.sdsolutions.particle.security.config.impl;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import io.sdsolutions.particle.security.filter.impl.OAuth2AutoLoginFilter;
import io.sdsolutions.particle.security.config.SecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

public class OAuth2SecurityConfiguration extends SecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2SecurityConfiguration.class);

    @Autowired
    private Environment environment;

    @Override
    protected AutoLoginFilter getAutoLoginFilter() {
        if (this.autoLoginFilter == null) {
            try {
                this.autoLoginFilter = new OAuth2AutoLoginFilter(super.authenticationManagerBean(), configurableJWTProcessor(), this.environment);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return this.autoLoginFilter;
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
}

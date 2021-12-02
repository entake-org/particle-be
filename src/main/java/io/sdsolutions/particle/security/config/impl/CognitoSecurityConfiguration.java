package io.sdsolutions.particle.security.config.impl;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import io.sdsolutions.particle.security.filter.impl.CognitoAutoLoginFilter;
import io.sdsolutions.particle.security.config.SecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

public class CognitoSecurityConfiguration extends SecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CognitoSecurityConfiguration.class);

    @Autowired
    private Environment environment;

    @Override
    protected AutoLoginFilter getAutoLoginFilter() {
        if (this.autoLoginFilter == null) {
            try {
                this.autoLoginFilter = new CognitoAutoLoginFilter(super.authenticationManagerBean(),
                                                                    configurableJWTProcessor(),
                                                                    this.environment);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return this.autoLoginFilter;
    }

    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(
                environment.getRequiredProperty("security.cognito.timeout.connection", Integer.class),
                environment.getRequiredProperty("security.cognito.timeout.read", Integer.class));

        URL jwkSetURL = new URL(String.format(
                        environment.getRequiredProperty("security.cognito.identity_pool_url") +
                        environment.getRequiredProperty("security.cognito.jwks_suffix"),
                        environment.getRequiredProperty("security.cognito.region"),
                        environment.getRequiredProperty("security.cognito.userpool")));

        JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        return jwtProcessor;
    }
}

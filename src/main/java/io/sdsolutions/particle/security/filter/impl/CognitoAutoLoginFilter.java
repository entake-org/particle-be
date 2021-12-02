package io.sdsolutions.particle.security.filter.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import io.sdsolutions.particle.security.filter.AutoLoginFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CognitoAutoLoginFilter extends AutoLoginFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CognitoAutoLoginFilter.class);

    private static final String DUMMY_USER = "user";
    private static final String DUMMY_PASSWORD = "password";
    private static final String ROLE_PREFIX = "ROLE_";

    private final ConfigurableJWTProcessor configurableJWTProcessor;
    private final Environment environment;

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return DUMMY_PASSWORD;
    }

    public CognitoAutoLoginFilter(AuthenticationManager authenticationManager, ConfigurableJWTProcessor configurableJWTProcessor, Environment environment) {
        super.setAuthenticationManager(authenticationManager);
        this.configurableJWTProcessor = configurableJWTProcessor;
        this.environment = environment;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return DUMMY_USER;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws ServletException {
        try {
            super.successfulAuthentication(request, response, handleAuthentication(request));
        } catch (IOException e) {
            throw new PreAuthenticatedCredentialsNotFoundException(HttpStatus.UNAUTHORIZED.toString());
        }
    }

    private Authentication handleAuthentication(HttpServletRequest request) throws IOException {
        String idToken = getToken(request);
        JWTClaimsSet claimsSet;

        try {
            claimsSet = configurableJWTProcessor.process(idToken, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException(e);
        }

        if (!isIssuedCorrectly(claimsSet)) {
            throw new IOException(String.format("Issuer %s in JWT token doesn't match cognito idp %s", claimsSet.getIssuer(), environment.getRequiredProperty("security.cognito.userpool")));
        }

        if (!isIdToken(claimsSet)) {
            throw new IOException("JWT Token doesn't seem to be an ID Token");
        }

        String username = claimsSet.getClaims().get(environment.getRequiredProperty("security.cognito.userclaim")).toString();

        if (username != null) {
            List<String> groups = (List<String>) claimsSet.getClaims().get(environment.getRequiredProperty("security.cognito.groupsclaim"));
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            if (groups != null && !groups.isEmpty()) {
                grantedAuthorities = convertList(groups, group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()));
            }

            PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(username, DUMMY_PASSWORD, grantedAuthorities);
            token.setDetails(idToken);
            return token;
        }

        return null;
    }

    private String getToken(HttpServletRequest request) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new IOException();
        }

        return authorizationHeader.replace("Bearer ", "");
    }

    private boolean isIssuedCorrectly(JWTClaimsSet claimsSet) {
        return claimsSet.getIssuer().equals(String.format(
                environment.getRequiredProperty("security.cognito.identity_pool_url"),
                environment.getRequiredProperty("security.cognito.region"),
                environment.getRequiredProperty("security.cognito.userpool")));
    }

    private boolean isIdToken(JWTClaimsSet claimsSet) {
        return claimsSet.getClaim("token_use").equals("id");
    }

    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }


}

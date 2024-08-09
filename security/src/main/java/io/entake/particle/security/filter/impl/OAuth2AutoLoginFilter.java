package io.entake.particle.security.filter.impl;

import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import io.entake.particle.security.config.SecurityConfiguration;
import io.entake.particle.security.constants.AntMatchers;
import io.entake.particle.security.filter.AutoLoginFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OAuth2AutoLoginFilter extends AutoLoginFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AutoLoginFilter.class);

    private static final String DUMMY_USER = "user";
    private static final String DUMMY_PASSWORD = "password";
    private static final String ROLE_PREFIX = "ROLE_";

    private final ConfigurableJWTProcessor<SimpleSecurityContext> configurableJWTProcessor;
    private final Environment environment;

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return DUMMY_PASSWORD;
    }

    public OAuth2AutoLoginFilter(AuthenticationManager authenticationManager, ConfigurableJWTProcessor<SimpleSecurityContext> configurableJWTProcessor, Environment environment) {
        super.setAuthenticationManager(authenticationManager);
        this.configurableJWTProcessor = configurableJWTProcessor;
        this.environment = environment;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isPermitted((HttpServletRequest) request)) {
            chain.doFilter(request, response);
        } else {
            super.doFilter(request, response, chain);
        }
    }

    private boolean isPermitted(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        uri = uri.substring(uri.indexOf('/', 1));

        for (String urlPattern : getPublicUrlPatterns()) {
            String urlStartsWith = StringUtils.replace(urlPattern, "/**", "");

            if (uri.startsWith(urlStartsWith)) {
                return true;
            }
        }

        return false;
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
            LOGGER.error(e.getMessage(), e);
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
            throw new IOException(String.format("Issuer (%s) in JWT token doesn't match IDP", claimsSet.getIssuer()));
        }

        String username = claimsSet.getClaims().get(environment.getRequiredProperty("security.oauth2.userclaim")).toString();

        if (username != null) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            if (StringUtils.isNotBlank(environment.getProperty("security.oauth2.groupsclaim"))) {
                List<String> groups = (List<String>) claimsSet.getClaims().get(environment.getProperty("security.oauth2.groupsclaim"));

                if (groups != null && !groups.isEmpty()) {
                    grantedAuthorities = convertList(groups, group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()));
                }
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
        return claimsSet.getIssuer().equals(environment.getRequiredProperty("security.oauth2.identity_pool_url"));
    }

    private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    protected String[] getPublicUrlPatterns() {
        String urlList = environment.getProperty(SecurityConfiguration.PUBLIC_URLS_PROPERTY);
        if (StringUtils.isNotBlank(urlList)) {
            return urlList.split(",");
        }

        return AntMatchers.PERMITTED_URLS;
    }

}

package io.entake.particle.security.services;

import io.entake.particle.security.models.OAuthUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class OAuthUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthUserDetailsService.class);

    private final RestTemplate restTemplate;
    private final Environment environment;

    public OAuthUserDetailsService(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    public OAuthUserDetails getUserDetails() {
        String idToken = SecurityContextHolder.getContext().getAuthentication().getDetails().toString();

        String issuer = environment.getRequiredProperty("security.oauth2.identity_pool_url");
        Map<String, Object> configuration = restTemplate.getForObject(issuer + "/.well-known/openid-configuration", Map.class);

        if (configuration != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", idToken));

            String url = configuration.get("userinfo_endpoint").toString();

            try {
                return restTemplate.exchange(new RequestEntity<>(new Object(), headers, HttpMethod.GET, new URI(url)), OAuthUserDetails.class).getBody();
            } catch (URISyntaxException e) {
                LOGGER.error("Error while getting user info endpoint", e);
            }
        }

        return null;
    }


}

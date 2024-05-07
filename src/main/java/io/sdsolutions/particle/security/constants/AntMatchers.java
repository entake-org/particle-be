package io.sdsolutions.particle.security.constants;

public class AntMatchers {

    public static final String[] PERMITTED_URLS = {
            "/api/public/**",
            "/actuator/**"
    };

    public static final String[] AUTHENTICATED_URLS = {
            "/api/**"
    };

}

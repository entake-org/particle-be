package io.sdsolutions.particle.smartystreets.config;

import com.smartystreets.api.ClientBuilder;
import com.smartystreets.api.us_street.Client;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class SmartyStreetsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartyStreetsConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnProperty(name = "smartystreets.enabled", havingValue = "true")
    public Client smartyStreetsClient() {
        String authId = environment.getProperty("smartystreets.auth.id");
        String authToken = environment.getProperty("smartystreets.auth.token");

        if (StringUtils.isBlank(authId)) {
            LOGGER.info("No SmartyStreets auth-id (smartystreets.auth.id) Provided.");
        }

        if (StringUtils.isBlank(authToken)) {
            LOGGER.info("No SmartyStreets auth-token (smartystreets.auth.token) Provided.");
        }

        return new ClientBuilder(authId, authToken).buildUsStreetApiClient();
    }


}

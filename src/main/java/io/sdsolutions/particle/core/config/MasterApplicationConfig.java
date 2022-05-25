package io.sdsolutions.particle.core.config;

import java.util.*;

import javax.servlet.MultipartConfigElement;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import io.sdsolutions.particle.core.dozer.BooleanStringConverter;
import io.sdsolutions.particle.core.dozer.LocalDateToUtilDateConverter;
import io.sdsolutions.particle.core.interceptor.JsonHijackingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a master configuration that makes it easy to get off the ground with a new application.
 * Rather than writing your own, you can merely extend this one to get the basic stuff you'll need to
 * do most standard stuff.
 * <p>
 * This includes stuff like Jackson and Dozer Mappers, as well as a MultipartConfigElement and a RestTemplate.
 * Your extended AppConfig can house anything else you need to run your app.
 */
public class MasterApplicationConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterApplicationConfig.class);

    @Autowired
    private JsonHijackingInterceptor jsonHijackingInterceptor;

    @Autowired
    private Environment environment;

    @Bean
    public Mapper dozerBeanMapper() {
        return DozerBeanMapperBuilder.create()
                .withMappingFiles(getMappingFiles())
                .withCustomConverter(new BooleanStringConverter())
                .withCustomConverter(new LocalDateToUtilDateConverter())
                .build();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        return factory.createMultipartConfig();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    protected List<String> getMappingFiles() {
        return Collections.emptyList();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonHijackingInterceptor);

        for (HandlerInterceptor i : getInterceptors()) {
            registry.addInterceptor(i);
        }
    }

    protected List<HandlerInterceptor> getInterceptors() {
        return new ArrayList<>();
    }

}

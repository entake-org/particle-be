package io.sdsolutions.particle.core.config;


import jakarta.servlet.MultipartConfigElement;

import io.sdsolutions.particle.core.interceptor.JsonHijackingInterceptor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a master configuration that makes it easy to get off the ground with a new application.
 * Rather than writing your own, you can merely extend this one to get the basic stuff you'll need to
 * do most standard stuff.
 * <p>
 * This includes stuff like Jackson and Dozer Mappers, as well as a MultipartConfigElement and a RestTemplate.
 * Your extended AppConfig can house anything else you need to run your app.
 */
public class MasterApplicationConfig implements WebMvcConfigurer {

    @Autowired
    private JsonHijackingInterceptor jsonHijackingInterceptor;

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

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        for (Converter<?, ?> converter : getModelMapperConverters()) {
            modelMapper.addConverter(converter);
        }

        return modelMapper;
    }

    protected List<Converter<?, ?>> getModelMapperConverters() {
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

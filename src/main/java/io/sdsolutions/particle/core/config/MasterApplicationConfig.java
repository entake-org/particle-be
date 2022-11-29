package io.sdsolutions.particle.core.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import jakarta.servlet.MultipartConfigElement;

import io.sdsolutions.particle.core.interceptor.JsonHijackingInterceptor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
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

    protected Map<String, Converter<?, ?>> availableModelMapperConverters() {
        AbstractConverter<String, Boolean> stringBooleanConverter = new AbstractConverter<>() {
            @Override
            protected Boolean convert(String source) {
                if (source != null) {
                    if (source.toLowerCase().startsWith("y")) {
                        return Boolean.TRUE;
                    } else if (source.toLowerCase().startsWith("n")) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }
        };

        AbstractConverter<Boolean, String> booleanStringConverter = new AbstractConverter<>() {
            @Override
            protected String convert(Boolean source) {
                return source != null && (source) ? "Yes" : "No";
            }
        };

        AbstractConverter<LocalDateTime, Date> localDateTimeDateConverter = new AbstractConverter<>() {
            @Override
            protected Date convert(LocalDateTime source) {
                if (source == null) {
                    return null;
                }

                return Date.from(source.toInstant(ZoneOffset.UTC));
            }
        };

        AbstractConverter<Date, LocalDateTime> dateLocalDateTimeConverter = new AbstractConverter<>() {
            @Override
            protected LocalDateTime convert(Date source) {
                if (source == null) {
                    return null;
                }

                return Instant.ofEpochMilli(source.getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
            }
        };

        Map<String, Converter<?, ?>> converters = new HashMap<>();
        converters.put("stringBoolean", stringBooleanConverter);
        converters.put("booleanString", booleanStringConverter);
        converters.put("localDateTimeDate", localDateTimeDateConverter);
        converters.put("dateLocalDateTime", dateLocalDateTimeConverter);

        return converters;
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

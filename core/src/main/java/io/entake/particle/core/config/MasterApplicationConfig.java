package io.entake.particle.core.config;


import jakarta.servlet.MultipartConfigElement;

import io.entake.particle.core.interceptor.JsonHijackingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public RestTemplate restTemplate(JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter) {
        final RestTemplate restTemplate = new RestTemplate();

        //find and replace Jackson message converter with our own
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            final HttpMessageConverter<?> httpMessageConverter = restTemplate.getMessageConverters().get(i);
            if (httpMessageConverter instanceof JacksonJsonHttpMessageConverter){
                restTemplate.getMessageConverters().set(i, jacksonJsonHttpMessageConverter);
            }
        }

        return restTemplate;
    }

    @Bean
    public JacksonJsonHttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new JacksonJsonHttpMessageConverter();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(getDateFormat());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new ValueSerializer<>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                gen.writeString(FORMATTER.format(value));
            }
        });

        simpleModule.addDeserializer(LocalDateTime.class, new ValueDeserializer<>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
                if (StringUtils.isBlank(p.getValueAsString())) {
                    return null;
                }

                if (NumberUtils.isCreatable(p.getValueAsString())) {
                    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(p.getValueAsString())), ZoneOffset.UTC).toLocalDateTime();
                } else {
                    return ZonedDateTime.parse(p.getValueAsString()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                }
            }
        });

        objectMapper.registeredModules().add(simpleModule);

        return objectMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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

    protected String getDateFormat() {
        return "yyyy-MM-dd'T'HH:mm:ss'Z'";
    }

}

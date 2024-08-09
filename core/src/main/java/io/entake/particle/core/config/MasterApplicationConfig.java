package io.entake.particle.core.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.servlet.MultipartConfigElement;

import io.entake.particle.core.interceptor.JsonHijackingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        final RestTemplate restTemplate = new RestTemplate();

        //find and replace Jackson message converter with our own
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            final HttpMessageConverter<?> httpMessageConverter = restTemplate.getMessageConverters().get(i);
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter){
                restTemplate.getMessageConverters().set(i, mappingJackson2HttpMessageConverter);
            }
        }

        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(FORMATTER.format(value));
            }
        });

        simpleModule.addDeserializer(OffsetDateTime.class, new JsonDeserializer<>() {
            @Override
            public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (StringUtils.isBlank(p.getText())) {
                    return null;
                }

                if (NumberUtils.isCreatable(p.getText())) {
                    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(p.getText())), ZoneOffset.UTC);
                } else {
                    return ZonedDateTime.parse(p.getText()).withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
                }
            }
        });

        objectMapper.registerModule(simpleModule);

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

}

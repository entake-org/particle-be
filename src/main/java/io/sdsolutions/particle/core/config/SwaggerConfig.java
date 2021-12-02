package io.sdsolutions.particle.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;

import java.util.Date;

/**
 * This represents a basic Swagger configuration for all of your endpoints. Simply having this jar on your classpath
 * will automagically create your Swagger documentation and post it at /swagger-ui/index.html, when the app is up and running.
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

    private Environment environment;

    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Docket wilyApi() {
        String applicationName = environment.getProperty("app.description");
        applicationName = applicationName != null ? applicationName : "Application API";

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(Date.class,
                        String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(true)
                .enableUrlTemplating(true)
                .tags(new Tag(applicationName, "All APIs provided by this implementation."));
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.MODEL)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .showCommonExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }
}

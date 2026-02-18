package io.entake.particle.aws.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import io.entake.particle.aws.services.AmazonS3Service;
import io.entake.particle.aws.services.impl.AmazonS3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class AWSConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSConfig.class);

    private final Environment environment;

    public AWSConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(environment.getRequiredProperty("aws.s3.bucket.region")))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
    public AmazonS3Service amazonS3Service(S3Client s3Client) {
        return new AmazonS3ServiceImpl(s3Client);
    }

}

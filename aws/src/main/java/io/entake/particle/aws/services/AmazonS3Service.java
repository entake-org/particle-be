package io.entake.particle.aws.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AmazonS3Service {

    byte[] getDocumentFromS3(String bucket, String documentKey) throws IOException;

    void uploadDocumentToS3(MultipartFile file, String bucket, String documentKey) throws IOException;

    void deleteDocumentFromS3(String bucket, String documentKey);
}

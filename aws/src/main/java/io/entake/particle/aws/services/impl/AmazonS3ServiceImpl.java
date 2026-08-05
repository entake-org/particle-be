package io.entake.particle.aws.services.impl;

import io.entake.particle.aws.services.AmazonS3Service;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final S3Client s3Client;

    public AmazonS3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public byte[] getDocumentFromS3(String bucket, String documentKey) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(documentKey).build();
            return IOUtils.toByteArray(s3Client.getObject(getObjectRequest));
        } catch (S3Exception | IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void uploadDocumentToS3(MultipartFile file, String bucket, String documentKey) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(documentKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (S3Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void uploadDocumentToS3WithAcls(MultipartFile file, String bucket, String documentKey, boolean isPublic) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(documentKey)
                    .contentType(file.getContentType())
                    .acl(isPublic ? ObjectCannedACL.PUBLIC_READ : null)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (S3Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void deleteDocumentFromS3(String bucket, String documentKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(documentKey).build());
    }

}

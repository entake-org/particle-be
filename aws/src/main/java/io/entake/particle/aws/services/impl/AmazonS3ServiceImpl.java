package io.entake.particle.aws.services.impl;

import io.entake.particle.aws.services.AmazonS3Service;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final S3Client s3Client;

    public AmazonS3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public byte[] getDocumentFromS3(String bucket, String documentKey) throws IOException {
        try {
            // Get an object and print its contents.
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(documentKey).build();
            return IOUtils.toByteArray(s3Client.getObject(getObjectRequest));
        } catch (S3Exception | IOException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
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

            // Upload a file as a new object with ContentType and title specified.
            File newFile = toFile(file);
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(newFile));
            newFile.delete();
        } catch (S3Exception e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            throw new IOException(e);
        }
    }

    @Override
    public void deleteDocumentFromS3(String bucket, String documentKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(documentKey).build());
    }


    protected File toFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && multipartFile.getOriginalFilename() != null) {
            File convFile = new File(multipartFile.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();

            return convFile;
        }

        throw new IOException("Uploaded File Does Not Exist!");
    }
}

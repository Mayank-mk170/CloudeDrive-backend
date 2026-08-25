package com.cloudstorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
public class S3Service {

    private final S3Client s3Client;

    private final String bucketName;
    private final String region;
    private final String accessKey;
    private final String secretKey;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public S3Service(
            S3Client s3Client,

            @Value("${aws.s3.bucket-name}")
            String bucketName,

            @Value("${aws.s3.region}")
            String region,

            @Value("${aws.s3.access-key}")
            String accessKey,

            @Value("${aws.s3.secret-key}")
            String secretKey
    ) {

        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    // ==========================================
    // GENERATE SIGNED UPLOAD URL
    // ==========================================

    public String generateUploadUrl(
            String s3Key,
            String contentType
    ) {

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                );

        try (
                S3Presigner presigner =
                        S3Presigner.builder()
                                .region(
                                        Region.of(region)
                                )
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .build()
        ) {

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(contentType)
                            .build();


            PresignedPutObjectRequest presignedRequest =
                    presigner.presignPutObject(
                            request -> request
                                    .signatureDuration(
                                            Duration.ofMinutes(10)
                                    )
                                    .putObjectRequest(
                                            putObjectRequest
                                    )
                    );


            return presignedRequest
                    .url()
                    .toString();
        }
    }


    // ==========================================
    // GENERATE SIGNED DOWNLOAD URL
    // ==========================================

    public String generateDownloadUrl(
            String s3Key
    ) {

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                );

        try (
                S3Presigner presigner =
                        S3Presigner.builder()
                                .region(
                                        Region.of(region)
                                )
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .build()
        ) {

            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .build();


            GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(
                                    Duration.ofMinutes(10)
                            )
                            .getObjectRequest(
                                    getObjectRequest
                            )
                            .build();


            PresignedGetObjectRequest presignedRequest =
                    presigner.presignGetObject(
                            presignRequest
                    );


            return presignedRequest
                    .url()
                    .toString();
        }
    }


    // ==========================================
    // DELETE FILE FROM S3
    // ==========================================

    public void deleteFile(
            String s3Key
    ) {

        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

        s3Client.deleteObject(request);
    }
}
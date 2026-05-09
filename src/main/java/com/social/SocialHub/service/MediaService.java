package com.social.SocialHub.service;

import com.social.SocialHub.dto.MediaUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    public List<MediaUploadResponse> upload(
            List<MultipartFile> files
    ) {

        List<MediaUploadResponse> result =
                new ArrayList<>();

        for (MultipartFile file : files) {

            try {

                String ext =
                        Objects.requireNonNull(
                                file.getOriginalFilename()
                        ).substring(
                                file.getOriginalFilename()
                                        .lastIndexOf(".")
                        );

                String fileName =
                        UUID.randomUUID() + ext;

                PutObjectRequest request =
                        PutObjectRequest.builder()

                                .bucket(bucket)

                                .key(fileName)

                                .contentType(
                                        file.getContentType()
                                )

                                .build();

                s3Client.putObject(
                        request,
                        RequestBody.fromBytes(
                                file.getBytes()
                        )
                );

                String fileUrl =
                        publicUrl + "/" + fileName;

                String type =
                        file.getContentType()
                                .startsWith("video")
                                ? "VIDEO"
                                : "IMAGE";

                result.add(
                        new MediaUploadResponse(
                                fileName,
                                fileUrl,
                                type,
                                file.getSize()
                        )
                );

            } catch (IOException e) {

                throw new RuntimeException(
                        "Upload failed",
                        e
                );
            }
        }

        return result;
    }
}
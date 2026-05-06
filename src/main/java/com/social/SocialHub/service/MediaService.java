package com.social.SocialHub.service;

import com.social.SocialHub.dto.MediaUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MediaService {

    private static final String UPLOAD_DIR = "uploads/";

    public List<MediaUploadResponse> upload(List<MultipartFile> files) {

        List<MediaUploadResponse> result = new ArrayList<>();

        for (MultipartFile file : files) {

            try {
                String ext = Objects.requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf("."));

                String fileName = UUID.randomUUID() + ext;

                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());

                Files.write(path, file.getBytes());

                String type = file.getContentType().startsWith("video") ? "VIDEO" : "IMAGE";

                result.add(new MediaUploadResponse(
                        fileName,
                        "/uploads/" + fileName,   // 🔥 FIX
                        type,
                        file.getSize()
                ));

            } catch (IOException e) {
                throw new RuntimeException("Upload failed", e);
            }
        }

        return result;
    }
}
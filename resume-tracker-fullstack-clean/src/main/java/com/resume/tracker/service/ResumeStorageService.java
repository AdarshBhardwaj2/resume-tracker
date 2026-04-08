package com.resume.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ResumeStorageService {

    private final Path uploadRoot;

    public ResumeStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not initialize upload directory", exception);
        }
    }

    public StoredResume store(MultipartFile multipartFile) {
        String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String extension = "";
        int extensionIndex = originalFileName.lastIndexOf('.');
        if (extensionIndex >= 0) {
            extension = originalFileName.substring(extensionIndex);
        }
        String storedFileName = UUID.randomUUID() + extension;
        try {
            Files.copy(multipartFile.getInputStream(), uploadRoot.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store uploaded resume", exception);
        }
        return new StoredResume(originalFileName, storedFileName);
    }

    public void deleteIfPresent(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return;
        }
        try {
            Files.deleteIfExists(uploadRoot.resolve(storedFileName));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not delete stored resume", exception);
        }
    }

    public record StoredResume(String originalFileName, String storedFileName) {
    }
}

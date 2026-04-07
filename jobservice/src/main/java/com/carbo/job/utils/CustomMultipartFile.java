package com.carbo.job.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    public CustomMultipartFile(InputStream inputStream, String name, String originalFilename, String contentType) throws IOException {
        this.content = readContent(inputStream);
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    private byte[] readContent(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        try (InputStream inputStream = getInputStream()) {
            java.nio.file.Files.copy(inputStream, dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

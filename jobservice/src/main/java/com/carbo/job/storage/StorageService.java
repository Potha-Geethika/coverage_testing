package com.carbo.job.storage;

import com.carbo.job.exception.NotImplementedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(String folder, MultipartFile file) throws IOException;

    Stream<Path> loadAll() throws NotImplementedException;

    Path load(String fileName) throws NotImplementedException;

    Resource loadAsResource(String folder, String fileName) throws FileNotFoundException, NotImplementedException;

    FileSystemResource loadAsFileSystemResource(String folder, String fileName) throws FileNotFoundException;

    String loadFileAsString(String jobId) throws IOException;

    String loadFileAsBase64String(String jobId) throws IOException;

    void deleteAll() throws NotImplementedException;
}

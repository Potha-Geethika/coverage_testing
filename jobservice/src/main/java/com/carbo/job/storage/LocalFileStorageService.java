package com.carbo.job.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.carbo.job.config.UploadConfig;
import com.carbo.job.exception.NotImplementedException;
import com.carbo.job.utils.CustomMultipartFile;
import com.carbo.job.utils.FileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

@Service
public class LocalFileStorageService implements StorageService {
    private final UploadConfig uploadConfig;

    @Autowired
    public LocalFileStorageService(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    @Override
    public void init() {
    }

    @Override
    public void store(String jobId, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        File dir = new File(uploadConfig.getUploadLocation() + File.separator + "job");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String path = dir.getAbsolutePath() + File.separator + jobId+FileOperation.getFileExtension(file);
        File uploadFile = new File(path);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));
        outputStream.write(bytes);
        outputStream.close();
    }

    @Override
    public String loadFileAsString(String jobId) throws IOException{
        Path path = Paths.get(
            uploadConfig.getUploadLocation() + File.separator + "job/" + jobId + ".pdf");
        byte[] bytes = Files.readAllBytes(path);
        String s = new String(bytes);  
        return s;
    }

    @Override
    public String loadFileAsBase64String(String jobId) throws IOException {
        Path path = Paths.get(
                uploadConfig.getUploadLocation() + File.separator + "job/" + jobId + ".pdf");
        byte[] bytes = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public Stream<Path> loadAll() throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    public Path load(String fileName) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    public Resource loadAsResource(String folder, String fileName) throws FileNotFoundException, NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    public FileSystemResource loadAsFileSystemResource(String folder, String fileName) throws FileNotFoundException {
        File file = new File(uploadConfig.getUploadLocation() + File.separator + folder + File.separator + fileName);
        return new FileSystemResource(file);
    }

    @Override
    public void deleteAll() throws NotImplementedException {
        throw new NotImplementedException();
    }

    /**
     * Loads a file from the Azure Blob Storage as a MultipartFile object.
     *
     * @param organizationId The ID of the organization whose file needs to be loaded.
     * @param fileName       The name of the file to be loaded.
     * @return A MultipartFile object containing the loaded file.
     * @throws IOException If an error occurs while loading the file.
     */
    public MultipartFile loadAsFileSystemResource2(String organizationId, String fileName) throws IOException {

        String blobUri = uploadConfig.getAzureBlobUrl();
        String sasToken = uploadConfig.getSasToken();
        String containerName = uploadConfig.getContainerName();
        BlobClient blobClient = null;
        try {
            blobClient = new BlobClientBuilder().endpoint(blobUri).sasToken(sasToken).containerName(containerName).blobName(organizationId + "/" + fileName).buildClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream inputStream = blobClient.openInputStream();

        return convertInputStreamToMultipartFile(inputStream, fileName);
    }

    /**
     * Converts an input stream to a MultipartFile object using a custom implementation.
     *
     * @param inputStream The input stream containing the file data.
     * @param fileName    The name of the file to be represented in the MultipartFile object.
     * @return A MultipartFile object representing the file data from the input stream.
     * @throws IOException If an error occurs while reading from the input stream.
     */
    public MultipartFile convertInputStreamToMultipartFile(InputStream inputStream, String fileName) throws IOException {
        // Use the custom MultipartFile implementation
        return new CustomMultipartFile(inputStream, "file", fileName, "application/octet-stream");
    }

}

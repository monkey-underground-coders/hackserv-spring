package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.rest.exc.FileSizeLimitExceededException;
import com.a6raywa1cher.hackservspring.service.DiscService;
import com.a6raywa1cher.hackservspring.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DiscServiceImpl implements DiscService {

    @Value("${app.upload-dir}")
    private String masterPath;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String MaxFileSize;

    @Autowired
    public DiscServiceImpl(){};

    private int sizeToInt() {
        return Integer.parseInt(MaxFileSize.substring(0, MaxFileSize.lastIndexOf("K")));
    }

    private Path getPath(String relativePath){
        return Path.of(masterPath, relativePath);
    }

    @Override
    public String create(MultipartFile file) throws IOException, FileSizeLimitExceededException {
        if (file.getSize() > sizeToInt()){
            throw new FileSizeLimitExceededException();
        }
        String uuid = UUID.randomUUID().toString();
        String originalFilename = ServiceUtils.getFileExtension(file);
        uuid = String.join("/", uuid.substring(0, 2), uuid.substring(2, 4), uuid.substring(4, 6),
                uuid.substring(6)) + originalFilename.substring(originalFilename.lastIndexOf('.'));
        Path filePathToTransferInto = getPath(uuid);
        Files.createDirectories(getPath(uuid.substring(0, uuid.lastIndexOf('/'))));
        file.transferTo(filePathToTransferInto);
        return uuid;
    }
    
    @Override
    public void deleteResource(String relativePath) {
        getPath(relativePath).toFile().delete();
    }

    @Override
    public Resource getResource(String relativePath) {
        return new PathResource(getPath(relativePath));
    }
}

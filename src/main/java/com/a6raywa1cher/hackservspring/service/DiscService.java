package com.a6raywa1cher.hackservspring.service;
import com.a6raywa1cher.hackservspring.rest.exc.FileSizeLimitExceededException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DiscService {
    String create(MultipartFile file) throws IOException, FileSizeLimitExceededException;
    void deleteResource(String path);
    Resource getResource(String path);
}

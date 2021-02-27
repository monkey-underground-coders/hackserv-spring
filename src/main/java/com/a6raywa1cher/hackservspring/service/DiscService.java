package com.a6raywa1cher.hackservspring.service;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DiscService {
    String create(MultipartFile file) throws IOException;
    void deleteResource(String path);
    Resource getResource(String path);
}

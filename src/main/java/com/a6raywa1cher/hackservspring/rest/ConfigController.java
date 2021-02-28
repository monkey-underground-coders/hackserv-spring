package com.a6raywa1cher.hackservspring.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {
    @Value("${spring.servlet.multipart.max-file-size}")
    String maxFileSize;

    @GetMapping("/conf")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    public ResponseEntity<String> getMaxFileSize(){
        return ResponseEntity.ok(maxFileSize);
    }
}

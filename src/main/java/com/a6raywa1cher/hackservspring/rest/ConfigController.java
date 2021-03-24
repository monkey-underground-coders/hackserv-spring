package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.rest.res.GetConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${app.min-email-req}")
    private Integer minEmailReq;

    @Value("${app.max-email-duration}")
    private Integer maxEmailDuration;

    @GetMapping("/conf")
    @Operation(security = @SecurityRequirement(name = "jwt"))
    public ResponseEntity<GetConfigResponse> getConfig() {

        GetConfigResponse response = new GetConfigResponse();
        response.setMaxFileSize(maxFileSize);
        response.setMinEmailReq(minEmailReq);
        response.setMaxEmailDuration(maxEmailDuration);

        return ResponseEntity.ok(response);
    }
}

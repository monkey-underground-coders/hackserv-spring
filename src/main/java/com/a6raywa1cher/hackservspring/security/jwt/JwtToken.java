package com.a6raywa1cher.hackservspring.security.jwt;

import com.a6raywa1cher.hackservspring.model.VendorId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JwtToken {
    private String token;

    private LocalDateTime expiringAt;

    private long uid;

    private long refreshId;

    private VendorId vendorId;

    private String vendorSub;
}

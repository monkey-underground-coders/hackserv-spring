package com.a6raywa1cher.hackservspring.security.jwt.service;


import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.security.jwt.JwtRefreshPair;

public interface JwtRefreshPairService {
    JwtRefreshPair issue(User user);

    JwtRefreshPair issue(User user, VendorId vendorId, String vendorSub);
}

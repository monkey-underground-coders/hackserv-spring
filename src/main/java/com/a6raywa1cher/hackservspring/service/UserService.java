package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.VendorId;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserService {
    User create(UserRole userRole, VendorId vendorId, String vendorSub, String email);

    User create(UserRole userRole, String email, String password);

    User create(UserRole userRole, String email, String password, String fullName);

    Optional<User> getById(Long id);

    Stream<User> getById(Collection<Long> ids);

    Optional<User> getByEmail(String email);

	Optional<User> getByVendorIdOrEmail(VendorId vendorId, String vendorSub, String email);

	User editUser(User user, UserRole userRole, String email, String fullName);

	User editPassword(User user, String password);

	User setLastVisitAt(User user, ZonedDateTime at);

	User setVendorSub(User user, VendorId vendorId, String vendorSub);

	Optional<User> findFirstByUserRole(UserRole role);

	void deleteUser(User user);
}

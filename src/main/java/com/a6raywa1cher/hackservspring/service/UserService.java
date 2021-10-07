package com.a6raywa1cher.hackservspring.service;

import com.a6raywa1cher.hackservspring.dto.UserInfo;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.UserState;
import com.a6raywa1cher.hackservspring.model.VendorId;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserService {
	User create(UserRole userRole, VendorId vendorId, String vendorSub, String email);

	User create(UserRole userRole, String email, String password);

	Optional<User> getByEmailAndPassword(String email, String password);

	Optional<User> getById(Long id);

	Stream<User> getById(Collection<Long> ids);

	Optional<User> getByEmail(String email);

	Optional<User> getByVendorId(VendorId vendorId, String vendorSub);

	User editUser(User user, UserRole userRole, String email);

	User editUserInfo(User user, UserInfo userInfo);

	User editPassword(User user, String password);

	User editEmailValidated(User user, boolean expr);

	User setLastVisitAt(User user, ZonedDateTime at);

	User setVendorSub(User user, VendorId vendorId, String vendorSub);

	User editUserState(User user, UserState userState);

	Optional<User> findFirstByUserRole(UserRole role);

	User setDocumentResumePath(User user, String path);

	User deleteResume(User user);

	void deleteUser(User user);
}

package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.security.jwt.service.RefreshTokenService;
import com.a6raywa1cher.hackservspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;

	@Autowired
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder,
						   RefreshTokenService refreshTokenService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public User create(UserRole userRole, VendorId vendorId, String vendorSub, String email) {
		return create(userRole, email, null, null, vendorId, vendorSub);
	}

	@Override
	public User create(UserRole userRole, String email, String password) {
		return create(userRole, email, password, null);
	}

	@Override
	public User create(UserRole userRole, String email, String password, String fullName) {
		return create(userRole, email, password, fullName, null, null);
	}

	private User create(UserRole userRole, String email, String password, String fullName, VendorId vendorId, String vendorSub) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password != null ? passwordEncoder.encode(password) : null);
		user.setFullName(fullName);
		user.setUserRole(userRole);
		user.setCreatedAt(ZonedDateTime.now());
		user.setLastVisitAt(ZonedDateTime.now());
		if (vendorId != null)
			switch (vendorId) {
				case GITHUB -> user.setGithubId(vendorSub);
				case VK -> user.setVkId(vendorSub);
				case GOOGLE -> user.setGoogleId(vendorSub);
			}
		return repository.save(user);
	}

	@Override
	public Optional<User> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Stream<User> getById(Collection<Long> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false);
	}

	@Override
	public Optional<User> getByEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	public Optional<User> getByVendorId(VendorId vendorId, String vendorSub) {
		return switch (vendorId) {
			case VK -> repository.findByVkId(vendorSub);
			case GOOGLE -> repository.findByGoogleId(vendorSub);
			case GITHUB -> repository.findByGithubId(vendorSub);
		};
	}


	@Override
	public User editUser(User user, UserRole userRole, String email, String fullName) {
		user.setFullName(fullName);
		user.setEmail(email);
		return repository.save(user);
	}

	@Override
	public User editPassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		return repository.save(user);
	}

	@Override
	public User setLastVisitAt(User user, ZonedDateTime at) {
		user.setLastVisitAt(at);
		return repository.save(user);
	}

	@Override
	public User setVendorSub(User user, VendorId vendorId, String vendorSub) {
		if (this.getByVendorId(vendorId, vendorSub).isPresent()) {
			throw new IllegalArgumentException();
		}
		switch (vendorId) {
			case VK -> user.setVkId(vendorSub);
			case GOOGLE -> user.setGoogleId(vendorSub);
			case GITHUB -> user.setGithubId(vendorSub);
			default -> throw new RuntimeException();
		}
		return repository.save(user);
	}

	@Override
	public Optional<User> findFirstByUserRole(UserRole role) {
		return repository.findFirstByUserRole(role);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void deleteUser(User user) {
		refreshTokenService.invalidateAll(user);
		repository.delete(user);
	}
}

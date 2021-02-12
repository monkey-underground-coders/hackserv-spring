package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
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

	@Autowired
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	private int extractNumber(String name) {
		return Integer.parseInt(name.substring(name.lastIndexOf('#') + 1));
	}

	@Override
	public synchronized User create(UserRole userRole, String username, String name, String password, String registrationIp) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setName(name);
		user.setUserRole(userRole);
		user.setCreatedAt(ZonedDateTime.now());
		user.setCreatedIp(registrationIp);
		user.setLastVisitAt(ZonedDateTime.now());
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
	public Optional<User> getByUsername(String username) {
		return repository.findByUsername(username);
	}


	@Override
	public User editUser(User user, UserRole userRole, String username, String name) {
		user.setUsername(username);
		user.setName(name);
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
	public Optional<User> findFirstByUserRole(UserRole role) {
		return repository.findFirstByUserRole(role);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void deleteUser(User user) {
		repository.delete(user);
	}
}

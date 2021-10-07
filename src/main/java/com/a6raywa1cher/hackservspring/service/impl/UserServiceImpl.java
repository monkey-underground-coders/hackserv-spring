package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.dto.UserInfo;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.UserState;
import com.a6raywa1cher.hackservspring.model.VendorId;
import com.a6raywa1cher.hackservspring.model.repo.UserRepository;
import com.a6raywa1cher.hackservspring.security.jwt.service.RefreshTokenService;
import com.a6raywa1cher.hackservspring.service.DiscService;
import com.a6raywa1cher.hackservspring.service.TeamService;
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
	private final TeamService teamService;
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final DiscService discService;

	@Autowired
	public UserServiceImpl(TeamService teamService, UserRepository repository, PasswordEncoder passwordEncoder,
	                       RefreshTokenService refreshTokenService, DiscService discService) {
		this.teamService = teamService;
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenService = refreshTokenService;
		this.discService = discService;
	}

	@Override
	public User create(UserRole userRole, VendorId vendorId, String vendorSub, String email) {
		return create(userRole, email, null, vendorId, vendorSub);
	}

	@Override
	public User create(UserRole userRole, String email, String password) {
		return create(userRole, email, password, null, null);
	}

	@Override
	public Optional<User> getByEmailAndPassword(String email, String password) {
		return repository.findByEmail(email)
			.filter(u -> passwordEncoder.matches(password, u.getPassword()));
	}

	private User create(UserRole userRole, String email, String password, VendorId vendorId, String vendorSub) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password != null ? passwordEncoder.encode(password) : null);
		user.setUserState(UserState.REGISTERED);

		user.setUserRole(userRole);
		user.setCreatedAt(ZonedDateTime.now());
		user.setLastVisitAt(ZonedDateTime.now());
		if (vendorId != null) {
			user.setEmailValidated(true);
			switch (vendorId) {
				case GITHUB -> user.setGithubId(vendorSub);
				case VK -> user.setVkId(vendorSub);
				case GOOGLE -> user.setGoogleId(vendorSub);
			}
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
	public User editUser(User user, UserRole userRole, String email) {
		user.setEmail(email);
		user.setUserRole(userRole);
		return repository.save(user);
	}

	@Override
	public User editUserInfo(User user, UserInfo userInfo) {
		user.setFirstName(userInfo.getFirstName());
		user.setLastName(userInfo.getLastName());
		user.setMiddleName(userInfo.getMiddleName());
		user.setTelegram(userInfo.getTelegram());
		user.setDateOfBirth(userInfo.getDateOfBirth());
		user.setWorkPlace(userInfo.getWorkPlace());
		user.setOtherInfo(userInfo.getOtherInfo());
		user.setResume(userInfo.getResume());
		return repository.save(user);
	}

	@Override
    public User editPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return repository.save(user);
    }

    @Override
    public User editEmailValidated(User user, boolean expr) {
        user.setEmailValidated(expr);
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
	public User editUserState(User user, UserState userState) {
		user.setUserState(userState);
		return repository.save(user);
	}

	@Override
	public Optional<User> findFirstByUserRole(UserRole role) {
		return repository.findFirstByUserRole(role);
	}

	@Override
	public User setDocumentResumePath(User user, String path) {
		user.setDocumentResumePath(path);
		return repository.save(user);
	}

	@Override
	public User deleteResume(User user) {
		discService.deleteResource(user.getDocumentResumePath());
		user.setDocumentResumePath(null);
		return repository.save(user);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void deleteUser(User user) {
		if (user.getDocumentResumePath() != null) {
			this.deleteResume(user);
		}
		refreshTokenService.invalidateAll(user);
		if (user.getTeam() != null) {
			teamService.deleteMember(user.getTeam(), user);
		}
		if (teamService.getTeamRequestForUser(user).isPresent()) {
			teamService.deleteRequest(teamService.getTeamRequestForUser(user).get(), user);
		}
		repository.delete(user);
	}
}

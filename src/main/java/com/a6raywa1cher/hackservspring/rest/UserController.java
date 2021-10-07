package com.a6raywa1cher.hackservspring.rest;

import com.a6raywa1cher.hackservspring.dto.UserInfo;
import com.a6raywa1cher.hackservspring.model.User;
import com.a6raywa1cher.hackservspring.model.UserRole;
import com.a6raywa1cher.hackservspring.model.UserState;
import com.a6raywa1cher.hackservspring.rest.exc.*;
import com.a6raywa1cher.hackservspring.rest.req.*;
import com.a6raywa1cher.hackservspring.security.jwt.JwtRefreshPair;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtRefreshPairService;
import com.a6raywa1cher.hackservspring.service.DiscService;
import com.a6raywa1cher.hackservspring.service.EmailValidationService;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Objects;


@RestController
@RequestMapping("/user")
@Transactional(rollbackOn = Exception.class)
public class UserController {
	private final UserService userService;
	private final EmailValidationService emailValidationService;
	private final DiscService discService;
	private final JwtRefreshPairService jwtRefreshPairService;

	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;

	public UserController(UserService userService, DiscService discService,
						  EmailValidationService emailValidationService, JwtRefreshPairService jwtRefreshPairService) {
		this.userService = userService;
		this.emailValidationService = emailValidationService;
		this.discService = discService;
		this.jwtRefreshPairService = jwtRefreshPairService;
	}

	@GetMapping(value = "/{uid}/cv/", produces = "application/octet-stream")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	public ResponseEntity<Resource> getResume(@PathVariable long uid) throws UserNotExistsException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);

		Resource file = discService.getResource(user.getDocumentResumePath());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=" + user.getFullName() + user.getDateOfBirth() +
						Objects.requireNonNull(file.getFilename()).substring(file.getFilename().lastIndexOf('.'))).body(file);
	}

	@PostMapping(path = "/{uid}/cv/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	@JsonView(Views.DetailedInternal.class)
	public User createResume(@PathVariable long uid, @RequestParam("file") MultipartFile file) throws UserNotExistsException, IOException, FileSizeLimitExceededException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		if (maxFileSize.toBytes() < file.getSize()) {
			throw new FileSizeLimitExceededException();
		}
		if (user.getDocumentResumePath() != null) {
			deleteResumeDocument(uid);
		}

		return userService.setDocumentResumePath(user, discService.create(file));
	}

	@DeleteMapping(value = "/{uid}/cv/")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	@JsonView(Views.DetailedInternal.class)
	public User deleteResumeDocument(@PathVariable long uid) throws UserNotExistsException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		return userService.deleteResume(user);
	}

	@PostMapping("/create")
	@SecurityRequirements // erase jwt login
	@JsonView(Views.DetailedInternal.class)
	public User createUser(@RequestBody @Valid CreateUserRequest request) throws EmailAlreadyExistsException, MessagingException {
		if (userService.getByEmail(request.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException();
		}
		User user = userService.create(UserRole.USER, request.getEmail(), request.getPassword());
		emailValidationService.createToken(user);
		emailValidationService.sendMassage(user);
		return user;
	}

	@GetMapping("/{uid}")
	@JsonView(Views.Public.class)
	public User getUserPublic(@PathVariable long uid) throws UserNotExistsException {
		return userService.getById(uid).orElseThrow(UserNotExistsException::new);
	}

	@GetMapping("/{uid}/internal")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	@JsonView(Views.Internal.class)
	public User getUserInternal(@PathVariable long uid) throws UserNotExistsException {
		return userService.getById(uid).orElseThrow(UserNotExistsException::new);
	}

	@PutMapping("/{uid:[0-9]+}")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	@JsonView(Views.DetailedInternal.class)
	public User editUserInfo(@RequestBody @Valid PutUserInfoRequest request, @PathVariable long uid) throws UserNotExistsException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		UserInfo userInfo = new UserInfo();
		BeanUtils.copyProperties(request, userInfo);

		return userService.editUserInfo(user, userInfo);
	}


	@PostMapping("/{uid:[0-9]+}/email/req")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	public void sendEmailValidationToken(@PathVariable long uid) throws MessagingException, UserNotExistsException, TooManyValidationRequestsExсeption {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		if (emailValidationService.isLastSendWasRecently(user)) {
			throw new TooManyValidationRequestsExсeption();
		}
		emailValidationService.createToken(user);
		emailValidationService.sendMassage(user);
	}


	@PostMapping("/{uid:[0-9]+}/email/validate")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	public void validate(@RequestBody @Valid EmailValidationTokenRequest request, @PathVariable long uid) throws UserNotExistsException, TokenIsWrongException, TokenIsNotEnabledException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);

		if (user.getEmailValidationToken() == null) {
			throw new TokenNotExistsException();
		}
		if (!emailValidationService.isTokenEnable(user)) {
			throw new TokenIsNotEnabledException();
		}
		if (!emailValidationService.checkToken(user, request.getToken())) {
			throw new TokenIsWrongException();
		}
		userService.editEmailValidated(user, true);
		emailValidationService.delete(user);
	}

	@PostMapping("/{uid:[0-9]+}/email/validate_by_id")
	@SecurityRequirements // erase jwt login
	public JwtRefreshPair validateById(@RequestBody @Valid EmailValidationTokenIdRequest request, @PathVariable long uid) throws UserNotExistsException, TokenIsWrongException, TokenIsNotEnabledException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);

		if (user.getEmailValidationToken() == null) {
			throw new TokenNotExistsException();
		}
		if (!emailValidationService.isTokenEnable(user)) {
			throw new TokenIsNotEnabledException();
		}
		if (!emailValidationService.checkToken(user, request.getId())) {
			throw new TokenIsWrongException();
		}
		userService.editEmailValidated(user, true);
		emailValidationService.delete(user);
		return jwtRefreshPairService.issue(user);
	}

	@PostMapping("/{uid:[0-9]+}/user_filled_form")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	@JsonView(Views.Internal.class)
	public User userFilledForm(@PathVariable long uid) {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		if (!user.getUserState().equals(UserState.REGISTERED)) {
			throw new UserIsNotRegisteredException();
		}

		if (StringUtils.isAnyBlank(user.getFirstName(), user.getLastName(), user.getTelegram(), user.getWorkPlace()) ||
			user.getDateOfBirth() == null) {
			throw new UserNotFilledFormException();
		}
		return userService.editUserState(user, UserState.FILLED_FORM);
	}

	@PostMapping("/{uid:[0-9]+}/change_state")
	@PreAuthorize("@mvcAccessChecker.checkUserIsAdmin()")
	@JsonView(Views.Internal.class)
	public User changeUserSate(@RequestBody @Valid UserStateRequest request, @PathVariable long uid) {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);
		return userService.editUserState(user, request.getUserState());
	}

	@DeleteMapping("/{uid:[0-9]+}/delete")
	@PreAuthorize("@mvcAccessChecker.checkUserInternalInfoAccess(#uid)")
	public void delete(@PathVariable long uid) throws UserNotExistsException {
		User user = userService.getById(uid).orElseThrow(UserNotExistsException::new);

		userService.deleteUser(user);
	}

}

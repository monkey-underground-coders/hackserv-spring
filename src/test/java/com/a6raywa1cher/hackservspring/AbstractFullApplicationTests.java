package com.a6raywa1cher.hackservspring;

import com.a6raywa1cher.hackservspring.rest.req.CreateUserRequest;
import com.a6raywa1cher.hackservspring.rest.req.EmailValidationTokenRequest;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtTokenService;
import com.a6raywa1cher.hackservspring.security.rest.req.LoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test-application-integration.properties")
@EnableAutoConfiguration(exclude = {
	OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class,
	MailSenderAutoConfiguration.class
})
@AutoConfigureMockMvc
@SpringBootTest
public abstract class AbstractFullApplicationTests {
	public static final String AUTHORIZATION = "Authorization";

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected JwtTokenService jwtTokenService;

	protected String authorization = null;

	protected Long uid = null;

	protected String getJwt(String email, String password) throws Exception {
//		String authorization = "basic " + base64Encode(email + ":" + password);
		MvcResult result = mvc.perform(
			post("/auth/convert")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
			.andExpect(status().isOk())
			.andReturn();
		JsonNode objectNode = objectMapper.readTree(result.getResponse().getContentAsString());
		return objectNode.get("accessToken").asText();
	}

	protected void login(String email, String password) throws Exception {
		String jwt = getJwt(email, password);
		authorization = "jwt " + jwt;
		uid = getUserId(jwt);
	}

	protected long getUserId(String email, String password) throws Exception {
		return getUserId(getJwt(email, password));
	}

	protected long getUserId(String jwt) {
		return jwtTokenService.decode(jwt).orElseThrow().getUid();
	}

	protected ResultActions performGet(String url) throws Exception {
		Assertions.assertNotNull(authorization);
		Assertions.assertNotNull(uid);

		return mvc.perform(get(url)
			.header(AUTHORIZATION, authorization));
	}

	protected ResultActions performPost(String url) throws Exception {
		Assertions.assertNotNull(authorization);
		Assertions.assertNotNull(uid);

		return mvc.perform(post(url)
			.header(AUTHORIZATION, authorization));
	}

	protected ResultActions performPostWithBody(String url, Object body) throws Exception {
		Assertions.assertNotNull(authorization);
		Assertions.assertNotNull(uid);

		return mvc.perform(post(url)
			.header(AUTHORIZATION, authorization)
			.contentType(MediaType.APPLICATION_JSON)
			.content(
				objectMapper.writeValueAsString(body)
			));
	}

	protected ResultActions performPost(String urlTemplate, Object... vars) throws Exception {
		Assertions.assertNotNull(authorization);
		Assertions.assertNotNull(uid);

		return mvc.perform(post(urlTemplate, vars)
			.header(AUTHORIZATION, authorization));
	}

	protected ResultActions performPostWithBody(String urlTemplate, Object body, Object... vars) throws Exception {
		Assertions.assertNotNull(authorization);
		Assertions.assertNotNull(uid);

		return mvc.perform(post(urlTemplate, vars)
			.header(AUTHORIZATION, authorization)
			.contentType(MediaType.APPLICATION_JSON)
			.content(
				objectMapper.writeValueAsString(body)
			));
	}

	protected void register(String email, String password) throws Exception {
		userCreate(new CreateUserRequest(email, password))
			.andExpect(status().isOk());

		login(email, password);
	}

	// ================================================================================================
	// ------------------------------------------ REST CALLS ------------------------------------------
	// ================================================================================================

	// --------------------------------------------- auth ---------------------------------------------

	protected ResultActions authUser() throws Exception {
		return performGet("/auth/user");
	}

	// --------------------------------------------- user ---------------------------------------------

	protected ResultActions userCreate(CreateUserRequest request) throws Exception {
		return mvc.perform(post("/user/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(
				objectMapper.writeValueAsString(request)
			));
	}

	protected ResultActions userEmailValidate(EmailValidationTokenRequest request) throws Exception {
		return performPostWithBody("/user/{0}/email/validate", request, uid);
	}
}

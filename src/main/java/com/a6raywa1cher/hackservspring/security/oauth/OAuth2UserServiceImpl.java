package com.a6raywa1cher.hackservspring.security.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component("oauth2-user-service")
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final RestTemplate restTemplate = new RestTemplate();
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    private <T> T getForEntity(OAuth2UserRequest userRequest, String uri, Map<String, ?> queryParams, Map<String, String> headers, Class<T> tClass) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, tClass, queryParams);

        if (responseEntity.getBody() == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE),
                    String.format("Empty body or error on getting UserInfo from %s",
                            userRequest.getClientRegistration().getRegistrationId()));
        }
        return responseEntity.getBody();
    }

    private ArrayNode getForEntityArrayNode(OAuth2UserRequest userRequest, String uri, Map<String, ?> queryParams, Map<String, String> headers) {
        return getForEntity(userRequest, uri, queryParams, headers, ArrayNode.class);
    }

    private ObjectNode getForEntityObjectNode(OAuth2UserRequest userRequest, String uri, Map<String, ?> queryParams, Map<String, String> headers) {
        return getForEntity(userRequest, uri, queryParams, headers, ObjectNode.class);
    }

    private OAuth2User processVk(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> additionalParameters = userRequest.getAdditionalParameters();
        if (StringUtils.hasText(additionalParameters.getOrDefault("deactivated", "").toString())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("Unverified email"));
        }
        String vkId = (String) additionalParameters.get("user_id");
        String email = (String) additionalParameters.get("email");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", vkId);
        attributes.put("email", email);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.getForEntity(
                "https://api.vk.com/method/users.get?access_token={access_token}&v=5.103&user_ids={user_ids}&fields=photo_200",
                ObjectNode.class,
                Map.of("access_token", userRequest.getAccessToken().getTokenValue(),
                        "user_ids", vkId
                )
        );
        if (responseEntity.getBody() == null || !responseEntity.getBody().has("response")) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE),
                    String.format("Empty body or error on getting UserInfo from %s",
                            userRequest.getClientRegistration().getRegistrationId()));
        }
        JsonNode additionalInfo = responseEntity.getBody().get("response").get(0);
        attributes.put("name", String.join(" ", additionalInfo.get("first_name").asText(), additionalInfo.get("last_name").asText()));
        attributes.put("picture", additionalInfo.get("photo_200").asText());
        return new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                attributes, "sub");
    }

    private OAuth2User processGithub(OAuth2UserRequest userRequest) {
        Map<String, Object> additionalParameters = userRequest.getAdditionalParameters();
        String accessToken = (String) additionalParameters.get("access_token");

        ArrayNode emails = getForEntityArrayNode(
                userRequest,
                "https://api.github.com/user/emails",
                new HashMap<>(),
                Map.of("Authorization", "token " + accessToken));
        String email = StreamSupport.stream(emails.spliterator(), false)
                .filter(jsonNode -> jsonNode.get("verified").asBoolean())
                .sorted(Comparator.comparing(jsonNode -> jsonNode.get("primary").asBoolean(), Comparator.reverseOrder()))
                .map(jsonNode -> jsonNode.get("email").asText())
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST),
                        "There isn't any verified email"));
        OAuth2User semiUser = defaultOAuth2UserService.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", Integer.toString(semiUser.getAttribute("id")));
        attributes.putAll(semiUser.getAttributes());
        attributes.put("email", email);
        return new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                attributes, "sub");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return switch (userRequest.getClientRegistration().getClientName()) {
            case "vk.com" -> processVk(userRequest);
            case "GitHub" -> processGithub(userRequest);
            default -> throw new RuntimeException();
        };
    }
}

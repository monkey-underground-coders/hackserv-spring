package com.a6raywa1cher.hackservspring.security;

import com.a6raywa1cher.hackservspring.config.AppConfigProperties;
import com.a6raywa1cher.hackservspring.security.authentication.CustomAuthenticationEntryPoint;
import com.a6raywa1cher.hackservspring.security.authentication.CustomAuthenticationSuccessHandler;
import com.a6raywa1cher.hackservspring.security.component.LastVisitFilter;
import com.a6raywa1cher.hackservspring.security.jwt.JwtAuthenticationFilter;
import com.a6raywa1cher.hackservspring.security.jwt.service.BlockedRefreshTokensService;
import com.a6raywa1cher.hackservspring.security.jwt.service.JwtTokenService;
import com.a6raywa1cher.hackservspring.security.providers.JwtAuthenticationProvider;
import com.a6raywa1cher.hackservspring.security.providers.UsernamePasswordAuthenticationProvider;
import com.a6raywa1cher.hackservspring.service.UserService;
import com.a6raywa1cher.hackservspring.utils.AuthenticationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final UserService userService;

    private final AppConfigProperties appConfigProperties;

    private final JwtTokenService jwtTokenService;

    private final AuthenticationResolver authenticationResolver;

    private final PasswordEncoder passwordEncoder;

    private final BlockedRefreshTokensService blockedRefreshTokensService;

    @Autowired
    public SecurityConfig(UserService userService, JwtTokenService jwtTokenService,
                          AuthenticationResolver authenticationResolver, AppConfigProperties appConfigProperties,
                          PasswordEncoder passwordEncoder, BlockedRefreshTokensService blockedRefreshTokensService,
                          @Qualifier("oidc-user-service") OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService,
                          @Qualifier("oauth2-user-service") OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userService = userService;
        this.appConfigProperties = appConfigProperties;
        this.jwtTokenService = jwtTokenService;
        this.authenticationResolver = authenticationResolver;
        this.passwordEncoder = passwordEncoder;
        this.blockedRefreshTokensService = blockedRefreshTokensService;
        this.oidcUserService = oidcUserService;
        this.oAuth2UserService = oAuth2UserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(new JwtAuthenticationProvider(userService, blockedRefreshTokensService))
                .authenticationProvider(new UsernamePasswordAuthenticationProvider(userService, passwordEncoder));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/oauth2/**", "/error", "/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/user/create").permitAll()
                .antMatchers("/v3/api-docs/**", "/webjars/**", "/swagger-resources", "/swagger-resources/**",
                        "/swagger-ui.html", "/swagger-ui/**").permitAll()
                .antMatchers("/csrf").permitAll()
                .antMatchers("/ws-entry").permitAll()
                .antMatchers("/auth/convert").hasAuthority(SecurityConstants.CONVERTIBLE)
                .antMatchers("/auth/get_access").permitAll()
                .antMatchers("/auth/**").authenticated()
                .antMatchers("/favicon.ico").permitAll()
                .anyRequest().access("hasRole('USER') && hasAuthority('ENABLED')");
        http.cors()
                .configurationSource(corsConfigurationSource(appConfigProperties));
        http.httpBasic()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        http.formLogin();
        http.oauth2Login()
                .successHandler(customAuthenticationSuccessHandler)
                .userInfoEndpoint()
                .oidcUserService(oidcUserService)
                .userService(oAuth2UserService)
                .and()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient());
        http.oauth2Client();
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenService, authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new LastVisitFilter(userService, authenticationResolver), SecurityContextHolderAwareRequestFilter.class);
//		http.addFilterBefore(new CriticalActionLimiterFilter(criticalActionLimiterService), JwtAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new OAuth2AuthorizationCodeGrantRequestEntityConverter());

        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(map -> {
            String accessToken = map.get(OAuth2ParameterNames.ACCESS_TOKEN);

            OAuth2AccessToken.TokenType accessTokenType = OAuth2AccessToken.TokenType.BEARER; // vk issue

            Map<String, Object> additionalParameters = new HashMap<>();

            map.forEach(additionalParameters::put);

            OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken)
                    .tokenType(accessTokenType)
                    .additionalParameters(additionalParameters);
            if (map.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
                long expiresIn = Long.parseLong(map.get(OAuth2ParameterNames.EXPIRES_IN));
                builder.expiresIn(expiresIn);
            }

            return builder.build();
        });
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        accessTokenResponseClient.setRestOperations(restTemplate);
        return accessTokenResponseClient;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(AppConfigProperties appConfigProperties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(appConfigProperties.getCorsAllowedOrigins()));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PATCH", "PUT", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.arogut.homex.data.config;

import com.arogut.homex.data.auth.JwtAuthenticationManager;
import com.arogut.homex.data.auth.SecurityContextRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;
import java.util.regex.Pattern;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class DataSecurityConfig implements WebFluxConfigurer {

    private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/h2-console",
            // -- actuator
            "/actuator/**"
    };

    private static final String NOOP_PASSWORD_PREFIX = "{noop}";

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .headers()
                .frameOptions().disable()
                .and()
                .authorizeExchange()
                .pathMatchers(AUTH_WHITELIST).permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .cors()
                .disable()
                .securityContextRepository(securityContextRepository)
                .csrf()
                .disable()
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
    }

    @Bean
    @Primary
    public ReactiveAuthenticationManager reactiveAuthenticationManager(JwtAuthenticationManager jwtAuthenticationManager,
                                                                       UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager) {
        return new DelegatingReactiveAuthenticationManager(List.of(jwtAuthenticationManager, userDetailsRepositoryReactiveAuthenticationManager));
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(MapReactiveUserDetailsService mapReactiveUserDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(mapReactiveUserDetailsService);
    }

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(SecurityProperties properties,
                                                                    ObjectProvider<PasswordEncoder> passwordEncoder) {
        SecurityProperties.User user = properties.getUser();
        UserDetails userDetails = getUserDetails(user, getOrDeducePassword(user, passwordEncoder.getIfAvailable()));
        return new MapReactiveUserDetailsService(userDetails);
    }

    private UserDetails getUserDetails(SecurityProperties.User user, String password) {
        List<String> roles = user.getRoles();
        return User.withUsername(user.getName()).password(password).roles(StringUtils.toStringArray(roles)).build();
    }

    private String getOrDeducePassword(SecurityProperties.User user, PasswordEncoder encoder) {
        String password = user.getPassword();
        if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
            return password;
        }
        return NOOP_PASSWORD_PREFIX + password;
    }
}

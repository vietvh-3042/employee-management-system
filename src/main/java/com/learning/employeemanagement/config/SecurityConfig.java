package com.learning.employeemanagement.config;

import com.learning.employeemanagement.constant.ApiPaths;
import com.learning.employeemanagement.constant.ActuatorPaths;
import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.entity.Role;
import com.learning.employeemanagement.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<JwtAuthenticationFilter> jwtFilterProvider,
            @Value("${app.security.enabled:true}") boolean securityEnabled,
            @Value("${app.security.csrf-enabled:true}") boolean csrfEnabled) throws Exception {
        http.csrf(csrf -> {
            if (csrfEnabled) {
                csrf.ignoringRequestMatchers(ApiPaths.V1 + "/**");
            } else {
                csrf.disable();
            }
        })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> configureAuthorization(auth, securityEnabled));
        JwtAuthenticationFilter jwtFilter = jwtFilterProvider.getIfAvailable();
        if (jwtFilter != null) {
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }
        return http.build();
    }

    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
            boolean securityEnabled) {
        if (!securityEnabled) {
            auth.anyRequest().permitAll();
            return;
        }

        configurePublicEndpoints(auth);
        configureReadEndpoints(auth);
        configureAdminEndpoints(auth);
        auth.anyRequest().authenticated();
    }

    private void configurePublicEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(
                        ApiPaths.AUTH_ALL,
                        ApiPaths.HELLO,
                        ActuatorPaths.HEALTH,
                        WebPaths.LOGIN,
                        WebPaths.REGISTER,
                        WebPaths.LOGOUT)
                .permitAll();
    }

    private void configureReadEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(
                        HttpMethod.GET,
                        WebPaths.DEPARTMENT_LIST,
                        WebPaths.EMPLOYEE_LIST,
                        WebPaths.EMPLOYEE_STATISTICS)
                .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.EMPLOYEES_ALL, ApiPaths.EMPLOYEE_REPORTS_ALL)
                .hasAnyRole(Role.USER.name(), Role.ADMIN.name());
    }

    private void configureAdminEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(WebPaths.DEPARTMENT_LIST, WebPaths.EMPLOYEE_ADD, ActuatorPaths.ALL)
                .hasRole(Role.ADMIN.name())
                .requestMatchers(ApiPaths.EMPLOYEES_ALL, ApiPaths.DEPARTMENTS_ALL)
                .hasRole(Role.ADMIN.name());
    }
}

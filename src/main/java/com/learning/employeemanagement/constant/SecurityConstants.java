package com.learning.employeemanagement.constant;

public final class SecurityConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_COOKIE = "EMPLOYEE_AUTH_TOKEN";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_CLAIM = "role";

    private SecurityConstants() {
    }
}

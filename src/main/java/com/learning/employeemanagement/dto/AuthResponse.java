package com.learning.employeemanagement.dto;

public record AuthResponse(String token, String username, String role) {
}

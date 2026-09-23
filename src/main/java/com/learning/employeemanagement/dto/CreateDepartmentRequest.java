package com.learning.employeemanagement.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentRequest(
        @NotBlank(message = "Department name is required") String name) {
}

package com.learning.employeemanagement.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class UtilityService {

    private static final String EMPLOYEE_CODE_FORMAT = "EMP-%d-%05d";

    private final Clock applicationClock;

    public UtilityService(Clock applicationClock) {
        this.applicationClock = applicationClock;
    }

    public String formatGreeting(String name) {
        String normalizedName = name == null || name.isBlank() ? "Employee Management" : name.trim();
        return "Hello, %s!".formatted(normalizedName);
    }

    public String generateEmployeeCode(long employeeNumber) {
        if (employeeNumber < 1) {
            throw new IllegalArgumentException("Employee number must be positive");
        }

        LocalDate today = LocalDate.now(applicationClock);
        return EMPLOYEE_CODE_FORMAT.formatted(today.getYear(), employeeNumber);
    }
}

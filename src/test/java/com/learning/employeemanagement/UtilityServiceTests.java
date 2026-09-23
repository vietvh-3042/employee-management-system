package com.learning.employeemanagement;

import com.learning.employeemanagement.service.UtilityService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilityServiceTests {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-09-23T00:00:00Z"), ZoneOffset.UTC);
    private final UtilityService utilityService = new UtilityService(fixedClock);

    @Test
    void formatsGreetingWithProvidedName() {
        assertEquals("Hello, Viet!", utilityService.formatGreeting(" Viet "));
    }

    @Test
    void generatesEmployeeCode() {
        assertEquals("EMP-2026-00042", utilityService.generateEmployeeCode(42));
    }

    @Test
    void rejectsInvalidEmployeeNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> utilityService.generateEmployeeCode(0));
    }
}

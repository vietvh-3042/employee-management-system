package com.learning.employeemanagement.constant;

public final class ActuatorPaths {

    public static final String BASE = "/actuator";
    public static final String HEALTH = BASE + "/health";
    public static final String ALL = BASE + "/**";

    private ActuatorPaths() {
    }
}

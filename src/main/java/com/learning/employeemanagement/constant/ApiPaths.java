package com.learning.employeemanagement.constant;

public final class ApiPaths {

    public static final String V1 = "/api/v1";
    public static final String HELLO = V1 + "/hello";
    public static final String EMPLOYEES = V1 + "/employees";
    public static final String DEPARTMENTS = V1 + "/departments";
    public static final String EMPLOYEE_REPORTS = V1 + "/reports/employees";
    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String EMPLOYEES_ALL = EMPLOYEES + "/**";
    public static final String DEPARTMENTS_ALL = DEPARTMENTS + "/**";
    public static final String EMPLOYEE_REPORTS_ALL = EMPLOYEE_REPORTS + "/**";
    public static final String EMPLOYEE_COUNT = EMPLOYEE_REPORTS + "/count";
    public static final String EMPLOYEE_STATISTICS = EMPLOYEE_REPORTS + "/statistics";

    private ApiPaths() {
    }
}

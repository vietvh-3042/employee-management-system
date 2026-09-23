package com.learning.employeemanagement.dto;

import com.learning.employeemanagement.entity.Department;
import com.learning.employeemanagement.entity.Employee;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Long departmentId,
        String departmentName) {

    public static EmployeeResponse from(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName());
    }
}

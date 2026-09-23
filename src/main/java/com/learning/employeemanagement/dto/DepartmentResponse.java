package com.learning.employeemanagement.dto;

import com.learning.employeemanagement.entity.Department;

public record DepartmentResponse(Long id, String name) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}

package com.learning.employeemanagement.repository;

import com.learning.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Employee> findByDepartmentNameContainingIgnoreCase(String departmentName, Pageable pageable);

    Page<Employee> findByNameContainingIgnoreCaseAndDepartmentNameContainingIgnoreCase(
            String name, String departmentName, Pageable pageable);
}

package com.learning.employeemanagement.repository;

import com.learning.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Query("delete from Employee e where e.id = :id")
    int deleteByIdIfPresent(@Param("id") Long id);

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Employee> findByDepartmentNameContainingIgnoreCase(String departmentName, Pageable pageable);

    Page<Employee> findByNameContainingIgnoreCaseAndDepartmentNameContainingIgnoreCase(
            String name, String departmentName, Pageable pageable);
}

package com.learning.employeemanagement.service;

import com.learning.employeemanagement.constant.CacheNames;
import com.learning.employeemanagement.dto.EmployeeCountResponse;
import com.learning.employeemanagement.dto.DepartmentEmployeeCount;
import com.learning.employeemanagement.dto.EmployeeStatisticsResponse;
import com.learning.employeemanagement.repository.DepartmentEmployeeCountProjection;
import com.learning.employeemanagement.repository.DepartmentRepository;
import com.learning.employeemanagement.repository.EmployeeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeReportService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Cacheable(CacheNames.EMPLOYEE_COUNT)
    public EmployeeCountResponse countEmployees() {
        return new EmployeeCountResponse(employeeRepository.count());
    }

    @Cacheable(CacheNames.EMPLOYEE_STATISTICS)
    public EmployeeStatisticsResponse statistics() {
        var departments = departmentRepository.countEmployeesByDepartment().stream()
                .map(this::toDepartmentCount)
                .toList();
        return new EmployeeStatisticsResponse(employeeRepository.count(), departments);
    }

    private DepartmentEmployeeCount toDepartmentCount(DepartmentEmployeeCountProjection projection) {
        return new DepartmentEmployeeCount(projection.getDepartmentName(), projection.getEmployeeCount());
    }
}

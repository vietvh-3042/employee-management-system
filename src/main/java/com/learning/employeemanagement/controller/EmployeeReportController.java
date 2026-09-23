package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.ApiPaths;
import com.learning.employeemanagement.dto.EmployeeCountResponse;
import com.learning.employeemanagement.dto.EmployeeStatisticsResponse;
import com.learning.employeemanagement.service.EmployeeReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeReportController {

    private final EmployeeReportService employeeReportService;

    public EmployeeReportController(EmployeeReportService employeeReportService) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping(ApiPaths.EMPLOYEE_COUNT)
    public ResponseEntity<EmployeeCountResponse> countEmployees() {
        return ResponseEntity.ok(employeeReportService.countEmployees());
    }

    @GetMapping(ApiPaths.EMPLOYEE_STATISTICS)
    public ResponseEntity<EmployeeStatisticsResponse> statistics() {
        return ResponseEntity.ok(employeeReportService.statistics());
    }
}

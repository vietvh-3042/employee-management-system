package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.service.EmployeeReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeStatisticsViewController {

    private final EmployeeReportService employeeReportService;

    public EmployeeStatisticsViewController(EmployeeReportService employeeReportService) {
        this.employeeReportService = employeeReportService;
    }

    @GetMapping(WebPaths.EMPLOYEE_STATISTICS)
    public String statistics(Model model) {
        model.addAttribute("statistics", employeeReportService.statistics());
        return "employees/statistics";
    }
}

package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@Controller
public class EmployeeListViewController {

    private final EmployeeService employeeService;

    public EmployeeListViewController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(WebPaths.EMPLOYEE_LIST)
    public String list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 20, sort = {"name", "id"}, direction = Sort.Direction.ASC)
            Pageable pageable,
            Model model) {
        model.addAttribute("employees", employeeService.findAll(name, department, pageable));
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("department", department == null ? "" : department);
        return "employees/list";
    }
}

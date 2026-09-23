package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.dto.CreateDepartmentRequest;
import com.learning.employeemanagement.service.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class DepartmentViewController {

    private final DepartmentService departmentService;

    public DepartmentViewController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping(WebPaths.DEPARTMENT_LIST)
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        return "departments/list";
    }

    @PostMapping(WebPaths.DEPARTMENT_LIST)
    public String create(@RequestParam(defaultValue = "") String name, Model model) {
        try {
            departmentService.create(new CreateDepartmentRequest(name));
            return "redirect:" + WebPaths.DEPARTMENT_LIST;
        } catch (ResponseStatusException exception) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("error", exception.getReason());
            model.addAttribute("name", name);
            return "departments/list";
        }
    }
}

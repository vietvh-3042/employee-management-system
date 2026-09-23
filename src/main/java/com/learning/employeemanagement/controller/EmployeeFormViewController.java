package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.WebPaths;
import com.learning.employeemanagement.dto.EmployeeForm;
import com.learning.employeemanagement.service.DepartmentService;
import com.learning.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class EmployeeFormViewController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public EmployeeFormViewController(
            EmployeeService employeeService,
            DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping(WebPaths.EMPLOYEE_ADD)
    public String addForm(Model model) {
        populateFormModel(model, new EmployeeForm());
        return "employees/add";
    }

    @PostMapping(WebPaths.EMPLOYEE_ADD)
    public String add(
            @Valid @ModelAttribute("employeeForm") EmployeeForm employeeForm,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, employeeForm);
            return "employees/add";
        }

        try {
            employeeService.create(employeeForm.toRequest());
            return "redirect:" + WebPaths.EMPLOYEE_LIST;
        } catch (ResponseStatusException exception) {
            populateFormModel(model, employeeForm);
            model.addAttribute("error", exception.getReason());
            return "employees/add";
        }
    }

    private void populateFormModel(Model model, EmployeeForm employeeForm) {
        model.addAttribute("employeeForm", employeeForm);
        model.addAttribute("departments", departmentService.findAll());
    }
}

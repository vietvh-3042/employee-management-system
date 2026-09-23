package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.ApiPaths;
import com.learning.employeemanagement.dto.CreateDepartmentRequest;
import com.learning.employeemanagement.dto.DepartmentResponse;
import com.learning.employeemanagement.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.DEPARTMENTS)
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse department = departmentService.create(request);
        return ResponseEntity.created(URI.create(ApiPaths.DEPARTMENTS + "/" + department.id()))
                .body(department);
    }
}

package com.learning.employeemanagement.service;

import com.learning.employeemanagement.constant.CacheNames;
import com.learning.employeemanagement.dto.CreateDepartmentRequest;
import com.learning.employeemanagement.dto.DepartmentResponse;
import com.learning.employeemanagement.entity.Department;
import com.learning.employeemanagement.repository.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @CacheEvict(cacheNames = CacheNames.EMPLOYEE_STATISTICS, allEntries = true)
    public DepartmentResponse create(CreateDepartmentRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department name is required");
        }
        String name = request.name().trim();
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw duplicateDepartment();
        }
        DepartmentResponse response;
        try {
            response = DepartmentResponse.from(departmentRepository.save(new Department(name)));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateDepartment();
        }
        log.info("Created department id={}, name={}", response.id(), response.name());
        return response;
    }

    private ResponseStatusException duplicateDepartment() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Department name is already registered");
    }
}

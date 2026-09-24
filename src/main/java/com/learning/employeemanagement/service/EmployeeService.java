package com.learning.employeemanagement.service;

import com.learning.employeemanagement.constant.CacheNames;
import com.learning.employeemanagement.dto.CreateEmployeeRequest;
import com.learning.employeemanagement.dto.EmployeeResponse;
import com.learning.employeemanagement.entity.Department;
import com.learning.employeemanagement.entity.Employee;
import com.learning.employeemanagement.repository.DepartmentRepository;
import com.learning.employeemanagement.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Page<EmployeeResponse> findAll(String name, String department, Pageable pageable) {
        Pageable stablePageable = stablePageable(pageable);
        Page<Employee> employees;
        if (hasValue(name) && hasValue(department)) {
            employees = employeeRepository
                    .findByNameContainingIgnoreCaseAndDepartmentNameContainingIgnoreCase(
                            name, department, stablePageable);
        } else if (hasValue(name)) {
            employees = employeeRepository.findByNameContainingIgnoreCase(name, stablePageable);
        } else if (hasValue(department)) {
            employees = employeeRepository.findByDepartmentNameContainingIgnoreCase(department, stablePageable);
        } else {
            employees = employeeRepository.findAll(stablePageable);
        }
        return employees.map(EmployeeResponse::from);
    }

    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    @CacheEvict(cacheNames = {CacheNames.EMPLOYEE_COUNT, CacheNames.EMPLOYEE_STATISTICS}, allEntries = true)
    public EmployeeResponse create(CreateEmployeeRequest request) {
        validateEmployeeRequest(request);
        Employee employee = new Employee(
                request.name().trim(),
                request.email().trim(),
                findDepartment(request.departmentId()));
        EmployeeResponse response;
        try {
            response = EmployeeResponse.from(employeeRepository.save(employee));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateEmail(exception);
        }
        log.info("Created employee id={}, email={}", response.id(), response.email());
        return response;
    }

    @CacheEvict(cacheNames = {CacheNames.EMPLOYEE_COUNT, CacheNames.EMPLOYEE_STATISTICS}, allEntries = true)
    public EmployeeResponse update(Long id, CreateEmployeeRequest request) {
        validateEmployeeRequest(request);
        Employee employee = getEmployee(id);
        employee.update(
                request.name().trim(),
                request.email().trim(),
                findDepartment(request.departmentId()));
        EmployeeResponse response;
        try {
            response = EmployeeResponse.from(employeeRepository.save(employee));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateEmail(exception);
        }
        log.info("Updated employee id={}", response.id());
        return response;
    }

    @CacheEvict(cacheNames = {CacheNames.EMPLOYEE_COUNT, CacheNames.EMPLOYEE_STATISTICS}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        if (employeeRepository.deleteByIdIfPresent(id) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        log.info("Deleted employee id={}", id);
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private Department findDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
    }

    private void validateEmployeeRequest(CreateEmployeeRequest request) {
        if (request == null || !hasValue(request.name()) || !hasValue(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and email are required");
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private Pageable stablePageable(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Order.asc("name"));
        if (sort.getOrderFor("id") == null) {
            sort = sort.and(Sort.by(Sort.Order.asc("id")));
        }
        return PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), MAX_PAGE_SIZE), sort);
    }

    private ResponseStatusException duplicateEmail(DataIntegrityViolationException cause) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Employee email is already registered", cause);
    }
}

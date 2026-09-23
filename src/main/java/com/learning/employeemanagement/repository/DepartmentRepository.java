package com.learning.employeemanagement.repository;

import com.learning.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @org.springframework.data.jpa.repository.Query("select d.name as departmentName, count(e) as employeeCount "
            + "from Department d left join d.employees e group by d.id, d.name order by d.name")
    java.util.List<DepartmentEmployeeCountProjection> countEmployeesByDepartment();
}

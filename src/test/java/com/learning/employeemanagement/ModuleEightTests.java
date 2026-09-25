package com.learning.employeemanagement;

import com.learning.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ModuleEightTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void clearEmployees() {
        employeeRepository.deleteAll();
    }

    @Test
    void reportsEmployeeCountAndInvalidatesCacheAfterCreate() throws Exception {
        mockMvc.perform(get("/api/v1/reports/employees/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(0));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nguyen Van A\",\"email\":\"a@example.com\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/reports/employees/count"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"totalEmployees\":1}"));
    }

    @Test
    void exposesActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
    }
}

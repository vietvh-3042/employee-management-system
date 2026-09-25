package com.learning.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import com.learning.employeemanagement.repository.EmployeeRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void clearEmployees() {
        employeeRepository.deleteAll();
    }

    @Test
    void createsEmployeeAndReturnsItInList() throws Exception {
        String request = "{\"name\":\"Nguyen Van A\",\"email\":\"a@example.com\"}";

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/1"))
                .andExpect(content().json("{\"id\":1,\"name\":\"Nguyen Van A\",\"email\":\"a@example.com\"}"));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Nguyen Van A"))
                .andExpect(jsonPath("$.content[0].email").value("a@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void rejectsDuplicateEmployeeEmail() throws Exception {
        String request = "{\"name\":\"Nguyen Van A\",\"email\":\"same@example.com\"}";
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nguyen Van B\",\"email\":\"same@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Employee email is already registered"));
    }

    @Test
    void paginatesEmployeeResultsWithStableOrder() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Same Name\",\"email\":\"first@example.com\"}"));
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Same Name\",\"email\":\"second@example.com\"}"));

        mockMvc.perform(get("/api/v1/employees?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void rejectsInvalidEmployeeRequest() throws Exception {
        String request = "{\"name\":\"\",\"email\":\"invalid-email\"}";

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name is required"))
                .andExpect(jsonPath("$.errors.email").value("Email must be valid"));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body must be valid JSON"));
    }

    @Test
    void returnsBadRequestForNonNumericEmployeeId() throws Exception {
        mockMvc.perform(get("/api/v1/employees/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request parameter or path variable has an invalid type"));
    }

    @Test
    void deletingAnEmployeeTwiceReturnsNotFoundOnSecondRequest() throws Exception {
        String location = mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Delete Me\",\"email\":\"delete@example.com\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete(location))
                .andExpect(status().isNotFound());
    }
}

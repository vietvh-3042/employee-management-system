package com.learning.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeViewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersEmployeeListPage() throws Exception {
        mockMvc.perform(get("/employees/list"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("name", ""))
                .andExpect(model().attribute("department", ""))
                .andExpect(model().attributeExists("employees"))
                .andExpect(view().name("employees/list"));
    }

    @Test
    void rendersEmployeeAddPage() throws Exception {
        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeForm", "departments"))
                .andExpect(view().name("employees/add"));
    }

    @Test
    void redisplaysEmployeeFormWhenFieldsAreInvalid() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/employees/add")
                        .param("name", "")
                        .param("email", "invalid"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employeeForm", "departments"))
                .andExpect(view().name("employees/add"));
    }

    @Test
    void rendersStatisticsPage() throws Exception {
        mockMvc.perform(get("/employees/statistics"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("statistics"))
                .andExpect(view().name("employees/statistics"));
    }
}

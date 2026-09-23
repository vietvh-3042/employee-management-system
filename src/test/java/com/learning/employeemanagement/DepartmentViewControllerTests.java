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
class DepartmentViewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersDepartmentPage() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("departments"))
                .andExpect(view().name("departments/list"));
    }

    @Test
    void redisplaysDepartmentPageWhenNameIsBlank() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/departments")
                        .param("name", " "))
                .andExpect(status().isOk())
                .andExpect(model().attribute("name", " "))
                .andExpect(model().attributeExists("departments", "error"))
                .andExpect(view().name("departments/list"));
    }
}

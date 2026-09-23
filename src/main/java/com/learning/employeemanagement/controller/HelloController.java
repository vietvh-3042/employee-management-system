package com.learning.employeemanagement.controller;

import com.learning.employeemanagement.constant.ApiPaths;
import com.learning.employeemanagement.service.UtilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final UtilityService utilityService;

    public HelloController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @GetMapping(ApiPaths.HELLO)
    public String hello(@RequestParam(required = false) String name) {
        return utilityService.formatGreeting(name);
    }
}
